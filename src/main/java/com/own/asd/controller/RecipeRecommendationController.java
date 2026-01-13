package com.own.asd.controller;

import com.own.asd.model.nutrition.Recipe;
import com.own.asd.service.RecipeRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipe-recommendations")
public class RecipeRecommendationController {

    @Autowired
    private RecipeRecommendationService recipeRecommendationService;

    /**
     * 获取智能推荐食谱
     */
    @GetMapping("/child/{childId}/smart")
    public ResponseEntity<Map<String, Object>> getSmartRecommendations(
            @PathVariable Long childId,
            @RequestParam(required = false) String mealType) {

        try {
            Recipe.MealType type = null;
            if (mealType != null && !mealType.isEmpty()) {
                type = Recipe.MealType.valueOf(mealType.toUpperCase());
            }

            Map<String, Object> recommendations = recipeRecommendationService.getSmartRecommendations(childId, type);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "智能推荐生成成功");
            response.put("data", recommendations);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "餐次类型参数错误");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "智能推荐生成失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取个性化推荐
     */
    @GetMapping("/child/{childId}/personalized")
    public ResponseEntity<Map<String, Object>> getPersonalizedRecommendations(@PathVariable Long childId) {
        try {
            Map<String, Object> recommendations = recipeRecommendationService.getPersonalizedRecommendations(childId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "个性化推荐生成成功");
            response.put("data", recommendations);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "个性化推荐生成失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取随机推荐
     */
    @GetMapping("/random")
    public ResponseEntity<Map<String, Object>> getRandomRecommendations(
            @RequestParam(defaultValue = "5") int count) {

        try {
            List<Recipe> recipes = recipeRecommendationService.getRandomRecommendations(count);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "随机推荐获取成功");
            response.put("data", recipes);
            response.put("count", recipes.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "随机推荐获取失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 搜索食谱
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchRecipes(
            @RequestParam String keyword,
            @RequestParam(required = false) String mealType,
            @RequestParam(required = false) Integer maxDifficulty,
            @RequestParam(required = false) Integer maxCookingTime) {

        try {
            Recipe.MealType type = null;
            if (mealType != null && !mealType.isEmpty()) {
                type = Recipe.MealType.valueOf(mealType.toUpperCase());
            }

            List<Recipe> recipes = recipeRecommendationService.searchRecipes(
                    keyword, type, maxDifficulty, maxCookingTime);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "搜索完成");
            response.put("data", recipes);
            response.put("count", recipes.size());
            response.put("keyword", keyword);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "餐次类型参数错误");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "搜索失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取推荐统计信息
     */
    @GetMapping("/child/{childId}/stats")
    public ResponseEntity<Map<String, Object>> getRecommendationStats(@PathVariable Long childId) {
        try {
            // 这里可以添加一些统计信息的逻辑
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRecommendations", 0);
            stats.put("lastRecommendationTime", null);
            stats.put("preferredMealTypes", new String[0]);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "统计信息获取成功");
            response.put("data", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "统计信息获取失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

