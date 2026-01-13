package com.own.asd.service;

import com.own.asd.model.nutrition.FoodItem;
import com.own.asd.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FoodNutritionService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    /**
     * 初始化食物营养成分数据库
     */
    @Transactional
    public void initializeFoodDatabase() {
        // 常见食物营养成分数据
        List<FoodItem> commonFoods = Arrays.asList(
            createFoodItem("苹果", "水果", 52.0, 0.3, 13.8, 0.1, 2.4, 6.0, 0.004, 4.0),
            createFoodItem("香蕉", "水果", 89.0, 0.3, 22.8, 0.1, 2.6, 1.0, 0.008, 8.0),
            createFoodItem("橙子", "水果", 47.0, 0.1, 11.8, 0.1, 2.0, 27.0, 0.004, 4.0),
            createFoodItem("牛奶", "乳制品", 42.0, 1.0, 4.8, 0.2, 0.3, 1.0, 0.113, 0.0),
            createFoodItem("鸡蛋", "蛋类", 147.0, 12.7, 1.1, 0.7, 0.0, 0.0, 0.001, 0.0),
            createFoodItem("鸡胸肉", "肉类", 165.0, 31.0, 0.0, 3.6, 0.0, 0.0, 0.007, 0.0),
            createFoodItem("牛肉", "肉类", 250.0, 26.0, 0.0, 15.0, 0.0, 0.0, 0.028, 0.0),
            createFoodItem("米饭", "谷物", 130.0, 2.7, 28.0, 0.3, 0.3, 0.0, 0.003, 0.0),
            createFoodItem("面条", "谷物", 137.0, 4.8, 26.0, 2.1, 1.1, 0.0, 0.005, 0.0),
            createFoodItem("土豆", "蔬菜", 77.0, 2.0, 17.0, 0.1, 0.4, 10.0, 0.001, 0.0),
            createFoodItem("胡萝卜", "蔬菜", 41.0, 0.9, 9.6, 0.2, 2.8, 5.9, 0.003, 0.0),
            createFoodItem("菠菜", "蔬菜", 23.0, 2.9, 3.6, 0.4, 2.2, 28.1, 0.001, 0.0),
            createFoodItem("西兰花", "蔬菜", 34.0, 2.8, 6.6, 0.4, 2.6, 89.2, 0.003, 0.0),
            createFoodItem("三文鱼", "鱼类", 208.0, 20.0, 0.0, 13.0, 0.0, 0.0, 0.009, 0.5),
            createFoodItem("豆腐", "豆制品", 70.0, 8.1, 1.9, 4.2, 0.6, 0.3, 0.166, 0.0),
            createFoodItem("花生", "坚果", 567.0, 25.8, 16.1, 49.2, 0.0, 0.0, 0.004, 0.0),
            createFoodItem("燕麦", "谷物", 389.0, 16.9, 66.3, 6.9, 10.6, 0.0, 0.005, 0.0),
            createFoodItem("酸奶", "乳制品", 59.0, 3.3, 4.7, 3.2, 0.0, 0.0, 0.121, 0.0),
            createFoodItem("奶酪", "乳制品", 402.0, 25.0, 1.3, 33.1, 0.1, 0.0, 0.721, 0.0),
            createFoodItem("面包", "谷物", 265.0, 9.0, 49.0, 3.2, 2.7, 0.0, 0.004, 0.0)
        );

        // 保存到数据库
        for (FoodItem food : commonFoods) {
            if (!foodItemRepository.findByName(food.getName()).isPresent()) {
                foodItemRepository.save(food);
            }
        }
    }

    private FoodItem createFoodItem(String name, String category, Double calories, Double protein,
                                  Double carbohydrates, Double fat, Double fiber, Double vitaminC,
                                  Double calcium, Double vitaminD) {
        FoodItem food = new FoodItem();
        food.setName(name);
        food.setCategory(category);
        food.setCalories(calories);
        food.setProtein(protein);
        food.setCarbohydrates(carbohydrates);
        food.setFat(fat);
        food.setFiber(fiber);
        food.setVitaminC(vitaminC);
        food.setCalcium(calcium);
        food.setVitaminD(vitaminD);
        food.setIsActive(true);
        return food;
    }

    /**
     * 搜索食物
     */
    public List<FoodItem> searchFoods(String keyword, String category, Pageable pageable) {
        if (category != null && !category.isEmpty()) {
            return foodItemRepository.findByCategoryAndNameContainingIgnoreCaseAndIsActiveTrue(
                    category, keyword, pageable);
        } else {
            return foodItemRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword, pageable);
        }
    }

    /**
     * 根据类别获取食物
     */
    public List<FoodItem> getFoodsByCategory(String category) {
        return foodItemRepository.findByCategoryAndIsActiveTrueOrderByProteinDesc(category);
    }

    /**
     * 获取高蛋白食物
     */
    public List<FoodItem> getHighProteinFoods(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return foodItemRepository.findByIsActiveTrueOrderByProteinDesc(pageable);
    }

    /**
     * 获取高纤维食物
     */
    public List<FoodItem> getHighFiberFoods(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return foodItemRepository.findByIsActiveTrueOrderByFiberDesc(pageable);
    }

    /**
     * 获取高维生素C食物
     */
    public List<FoodItem> getHighVitaminCFoods(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return foodItemRepository.findByIsActiveTrueOrderByVitaminCDesc(pageable);
    }

    /**
     * 获取高钙食物
     */
    public List<FoodItem> getHighCalciumFoods(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return foodItemRepository.findByIsActiveTrueOrderByCalciumDesc(pageable);
    }

    /**
     * 根据营养需求推荐食物
     */
    public List<FoodItem> recommendFoodsByNutrition(String nutrient, int limit) {
        switch (nutrient.toLowerCase()) {
            case "protein":
                return getHighProteinFoods(limit);
            case "fiber":
                return getHighFiberFoods(limit);
            case "vitaminc":
                return getHighVitaminCFoods(limit);
            case "calcium":
                return getHighCalciumFoods(limit);
            default:
                return getAllActiveFoods(PageRequest.of(0, limit));
        }
    }

    /**
     * 获取所有活跃食物
     */
    public List<FoodItem> getAllActiveFoods(Pageable pageable) {
        Page<FoodItem> page = foodItemRepository.findByIsActiveTrue(pageable);
        return page.getContent();
    }

    /**
     * 获取食物类别统计
     */
    public Map<String, Long> getFoodCategoryStats() {
        List<Object[]> stats = foodItemRepository.countByCategoryAndIsActiveTrue();
        return stats.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    /**
     * 获取营养数据库统计信息
     */
    public Map<String, Object> getNutritionDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalFoods = foodItemRepository.countByIsActiveTrue();
        Map<String, Long> categoryStats = getFoodCategoryStats();

        stats.put("totalFoods", totalFoods);
        stats.put("categoryStats", categoryStats);
        stats.put("lastUpdated", new Date());

        return stats;
    }

    /**
     * 批量导入食物数据
     */
    @Transactional
    public void batchImportFoods(List<Map<String, Object>> foodDataList) {
        for (Map<String, Object> foodData : foodDataList) {
            try {
                FoodItem food = new FoodItem();
                food.setName((String) foodData.get("name"));
                food.setCategory((String) foodData.get("category"));
                food.setCalories(parseDouble(foodData.get("calories")));
                food.setProtein(parseDouble(foodData.get("protein")));
                food.setCarbohydrates(parseDouble(foodData.get("carbohydrates")));
                food.setFat(parseDouble(foodData.get("fat")));
                food.setFiber(parseDouble(foodData.get("fiber")));
                food.setVitaminC(parseDouble(foodData.get("vitaminC")));
                food.setCalcium(parseDouble(foodData.get("calcium")));
                food.setVitaminD(parseDouble(foodData.get("vitaminD")));
                food.setDescription((String) foodData.get("description"));
                food.setIsActive(true);

            } catch (Exception e) {
                // 记录错误但继续处理其他数据
                System.err.println("导入食物数据失败: " + foodData.get("name") + ", 错误: " + e.getMessage());
            }
        }
    }

    private Double parseDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * 获取食物营养详情
     */
    public Optional<FoodItem> getFoodNutritionDetails(Long foodId) {
        return foodItemRepository.findById(foodId);
    }

    /**
     * 更新食物营养信息
     */
    @Transactional
    public FoodItem updateFoodNutrition(Long foodId, Map<String, Object> nutritionData) {
        FoodItem food = foodItemRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("食物不存在"));

        if (nutritionData.containsKey("calories")) {
            food.setCalories(parseDouble(nutritionData.get("calories")));
        }
        if (nutritionData.containsKey("protein")) {
            food.setProtein(parseDouble(nutritionData.get("protein")));
        }
        if (nutritionData.containsKey("carbohydrates")) {
            food.setCarbohydrates(parseDouble(nutritionData.get("carbohydrates")));
        }
        if (nutritionData.containsKey("fat")) {
            food.setFat(parseDouble(nutritionData.get("fat")));
        }
        if (nutritionData.containsKey("fiber")) {
            food.setFiber(parseDouble(nutritionData.get("fiber")));
        }
        if (nutritionData.containsKey("vitaminC")) {
            food.setVitaminC(parseDouble(nutritionData.get("vitaminC")));
        }
        if (nutritionData.containsKey("calcium")) {
            food.setCalcium(parseDouble(nutritionData.get("calcium")));
        }
        if (nutritionData.containsKey("vitaminD")) {
            food.setVitaminD(parseDouble(nutritionData.get("vitaminD")));
        }
        if (nutritionData.containsKey("description")) {
            food.setDescription((String) nutritionData.get("description"));
        }

        return foodItemRepository.save(food);
    }
}

