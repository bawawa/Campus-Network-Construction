package com.own.asd.controller;

import com.own.asd.model.nutrition.FoodItem;
import com.own.asd.service.FoodNutritionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/food-nutrition")
public class FoodNutritionController {

    @Autowired
    private FoodNutritionService foodNutritionService;

    /**
     * 初始化食物数据库
     */
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initializeFoodDatabase() {
        try {
            foodNutritionService.initializeFoodDatabase();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "食物营养成分数据库初始化成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "食物数据库初始化失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 搜索食物
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchFoods(
            @RequestParam String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            List<FoodItem> foods = foodNutritionService.searchFoods(keyword, category, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", foods);
            response.put("count", foods.size());
            response.put("keyword", keyword);
            response.put("category", category);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "搜索食物失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据类别获取食物
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getFoodsByCategory(@PathVariable String category) {
        try {
            List<FoodItem> foods = foodNutritionService.getFoodsByCategory(category);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", foods);
            response.put("count", foods.size());
            response.put("category", category);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取食物失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取高蛋白食物
     */
    @GetMapping("/high-protein")
    public ResponseEntity<Map<String, Object>> getHighProteinFoods(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<FoodItem> foods = foodNutritionService.getHighProteinFoods(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", foods);
            response.put("count", foods.size());
            response.put("nutrient", "protein");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取高蛋白食物失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取高纤维食物
     */
    @GetMapping("/high-fiber")
    public ResponseEntity<Map<String, Object>> getHighFiberFoods(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<FoodItem> foods = foodNutritionService.getHighFiberFoods(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", foods);
            response.put("count", foods.size());
            response.put("nutrient", "fiber");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取高纤维食物失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取高维生素C食物
     */
    @GetMapping("/high-vitaminc")
    public ResponseEntity<Map<String, Object>> getHighVitaminCFoods(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<FoodItem> foods = foodNutritionService.getHighVitaminCFoods(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", foods);
            response.put("count", foods.size());
            response.put("nutrient", "vitaminC");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取高维生素C食物失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取高钙食物
     */
    @GetMapping("/high-calcium")
    public ResponseEntity<Map<String, Object>> getHighCalciumFoods(
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<FoodItem> foods = foodNutritionService.getHighCalciumFoods(limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", foods);
            response.put("count", foods.size());
            response.put("nutrient", "calcium");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取高钙食物失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据营养需求推荐食物
     */
    @GetMapping("/recommend")
    public ResponseEntity<Map<String, Object>> recommendFoodsByNutrition(
            @RequestParam String nutrient,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            List<FoodItem> foods = foodNutritionService.recommendFoodsByNutrition(nutrient, limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", foods);
            response.put("count", foods.size());
            response.put("nutrient", nutrient);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "营养推荐失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取食物营养详情
     */
    @GetMapping("/{foodId}")
    public ResponseEntity<Map<String, Object>> getFoodNutritionDetails(@PathVariable Long foodId) {
        try {
            Optional<FoodItem> food = foodNutritionService.getFoodNutritionDetails(foodId);

            if (food.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", food.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "食物不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取食物详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新食物营养信息
     */
    @PutMapping("/{foodId}")
    public ResponseEntity<Map<String, Object>> updateFoodNutrition(
            @PathVariable Long foodId,
            @RequestBody Map<String, Object> nutritionData) {

        try {
            FoodItem food = foodNutritionService.updateFoodNutrition(foodId, nutritionData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "食物营养信息更新成功");
            response.put("data", food);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新食物营养信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取营养数据库统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNutritionDatabaseStats() {
        try {
            Map<String, Object> stats = foodNutritionService.getNutritionDatabaseStats();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取统计信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量导入食物数据
     */
    @PostMapping("/batch-import")
    public ResponseEntity<Map<String, Object>> batchImportFoods(@RequestBody List<Map<String, Object>> foodDataList) {
        try {
            foodNutritionService.batchImportFoods(foodDataList);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量导入食物数据成功");
            response.put("importedCount", foodDataList.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批量导入失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

