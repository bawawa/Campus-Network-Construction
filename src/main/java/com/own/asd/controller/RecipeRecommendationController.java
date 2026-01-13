package com.own.asd.controller;

import com.own.asd.model.nutrition.Recipe;
import com.own.asd.service.AIReportService;
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

    @Autowired
    private AIReportService aiReportService;

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

    /**
     * 生成 AI 推荐食谱
     */
    @PostMapping("/child/{childId}/ai-recommend")
    public ResponseEntity<Map<String, Object>> generateAIRecommendations(
            @PathVariable Long childId,
            @RequestParam(required = false) String mealType) {

        try {
            Recipe.MealType type = null;
            if (mealType != null && !mealType.isEmpty()) {
                type = Recipe.MealType.valueOf(mealType.toUpperCase());
            }

            // 构建给 AI 的 prompt
            String prompt = buildRecipeRecommendationPrompt(childId, type);
            String aiRecommendation = aiReportService.generateCustomRecommendation(prompt);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "AI推荐生成成功");
            response.put("data", aiRecommendation);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "餐次类型参数错误");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "AI推荐生成失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 构建食谱推荐 prompt
     */
    private String buildRecipeRecommendationPrompt(Long childId, Recipe.MealType mealType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("作为专业的儿童营养师，请推荐适合ASD儿童的食谱。\n\n");

        if (mealType != null) {
            prompt.append("【餐次】").append(getMealTypeName(mealType)).append("\n");
        }

        prompt.append("【要求】\n");
        prompt.append("1. 推荐3道适合儿童的简单易做的食谱\n");
        prompt.append("2. 每道食谱包含：食谱名称、主要食材、制作方法、营养特点\n");
        prompt.append("3. 考虑ASD儿童的饮食特点（口感温和、颜色鲜艳、形状可爱）\n");
        prompt.append("4. 避免过敏原和高敏食材\n");
        prompt.append("5. 每道食谱标注适合的餐次\n");
        prompt.append("6. 简洁明了，每道食谱100字以内\n\n");

        return prompt.toString();
    }

    private String getMealTypeName(Recipe.MealType mealType) {
        switch (mealType) {
            case BREAKFAST: return "早餐";
            case LUNCH: return "午餐";
            case DINNER: return "晚餐";
            case SNACK: return "加餐";
            case SUPPER: return "夜宵";
            default: return "任意餐次";
        }
    }
}

