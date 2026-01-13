package com.own.asd.service;

import com.own.asd.model.nutrition.DietaryRecord;
import com.own.asd.model.nutrition.Recipe;
import com.own.asd.model.user.ASDProfile;
import com.own.asd.model.user.Child;
import com.own.asd.repository.RecipeRepository;
import com.own.asd.repository.DietaryRecordRepository;
import com.own.asd.repository.ChildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RecipeRecommendationService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private DietaryRecordRepository dietaryRecordRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private NutritionAnalysisService nutritionAnalysisService;

    /**
     * 智能推荐食谱
     */
    public Map<String, Object> getSmartRecommendations(Long childId, Recipe.MealType mealType) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("儿童信息未找到"));

        Map<String, Object> recommendations = new HashMap<>();

        // 获取儿童的基本信息和ASD特质
        ASDProfile asdProfile = child.getAsdProfile();
        int age = calculateAge(child.getBirthDate());

        // 获取最近饮食记录分析
        Map<String, Object> nutritionAnalysis = getRecentNutritionAnalysis(childId);

        // 基于ASD特质的食谱筛选
        List<Recipe> asdFriendlyRecipes = filterRecipesByASDProfile(asdProfile, mealType);

        // 基于营养需求的食谱推荐
        List<Recipe> nutritionBasedRecipes = recommendByNutritionNeeds(nutritionAnalysis, mealType);

        // 基于年龄的食谱推荐
        List<Recipe> ageAppropriateRecipes = filterByAgeGroup(asdFriendlyRecipes, age);

        // 基于饮食偏好的食谱推荐
        List<Recipe> preferenceBasedRecipes = recommendByPreferences(childId, mealType);

        // 综合评分和排序
        List<Recipe> finalRecommendations = combineAndScoreRecommendations(
                asdFriendlyRecipes, nutritionBasedRecipes, ageAppropriateRecipes, preferenceBasedRecipes);

        // 生成推荐理由
        List<Map<String, Object>> detailedRecommendations = generateDetailedRecommendations(
                finalRecommendations, child, nutritionAnalysis);

        recommendations.put("recommendations", detailedRecommendations);
        recommendations.put("totalCount", detailedRecommendations.size());
        recommendations.put("mealType", mealType);
        recommendations.put("childAge", age);
        recommendations.put("generatedAt", LocalDateTime.now());

        return recommendations;
    }

    /**
     * 基于ASD特质筛选食谱
     */
    private List<Recipe> filterRecipesByASDProfile(ASDProfile asdProfile, Recipe.MealType mealType) {
        List<Recipe> allRecipes = recipeRepository.findBySuitableMealTypeAndIsActiveTrue(mealType);

        if (asdProfile == null) {
            return allRecipes;
        }

        return allRecipes.stream()
                .filter(recipe -> isRecipeASDCompatible(recipe, asdProfile))
                .collect(Collectors.toList());
    }

    /**
     * 判断食谱是否适合ASD儿童
     */
    private boolean isRecipeASDCompatible(Recipe recipe, ASDProfile asdProfile) {
        // 基于感官敏感度的筛选
        String sensorySensitivity = asdProfile.getSensorySensitivity();
        if ("high".equalsIgnoreCase(sensorySensitivity)) {
            // 高敏感度儿童适合简单质地、少调料的食谱
            if (recipe.getTextureNotes() != null &&
                recipe.getTextureNotes().toLowerCase().contains("smooth")) {
                return true;
            }
            if (recipe.getDifficultyLevel() != null && recipe.getDifficultyLevel() <= 2) {
                return true;
            }
        }

        // 基于行为模式的筛选
        String behaviorPatterns = asdProfile.getBehaviorPatterns();
        if (behaviorPatterns != null && behaviorPatterns.toLowerCase().contains("routine")) {
            // 喜欢规律性的儿童适合熟悉的食谱
            return recipe.getIsPreset() != null && recipe.getIsPreset();
        }

        // 基于食物过敏的筛选
        if (asdProfile.getAllergies() != null && !asdProfile.getAllergies().isEmpty()) {
            String allergenWarnings = recipe.getAllergenWarnings();
            if (allergenWarnings != null) {
                for (String allergy : asdProfile.getAllergies()) {
                    if (allergenWarnings.toLowerCase().contains(allergy.toLowerCase())) {
                        return false; // 包含过敏原，不适合
                    }
                }
            }
        }

        return true;
    }

    /**
     * 基于营养需求推荐食谱
     */
    private List<Recipe> recommendByNutritionNeeds(Map<String, Object> nutritionAnalysis, Recipe.MealType mealType) {
        List<Recipe> allRecipes = recipeRepository.findBySuitableMealTypeAndIsActiveTrue(mealType);

        if (nutritionAnalysis == null || nutritionAnalysis.isEmpty()) {
            return allRecipes;
        }

        @SuppressWarnings("unchecked")
        Map<String, Double> nutritionSummary = (Map<String, Double>) nutritionAnalysis.get("nutritionSummary");
        @SuppressWarnings("unchecked")
        Map<String, Object> balanceAssessment = (Map<String, Object>) nutritionAnalysis.get("balanceAssessment");

        if (nutritionSummary == null || balanceAssessment == null) {
            return allRecipes;
        }

        // 找出需要补充的营养素
        @SuppressWarnings("unchecked")
        Map<String, String> status = (Map<String, String>) balanceAssessment.get("status");

        List<String> deficientNutrients = status.entrySet().stream()
                .filter(entry -> "不足".equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 优先推荐富含缺乏营养素的食谱
        return allRecipes.stream()
                .filter(recipe -> isRichInNutrients(recipe, deficientNutrients))
                .collect(Collectors.toList());
    }

    /**
     * 判断食谱是否富含特定营养素
     */
    private boolean isRichInNutrients(Recipe recipe, List<String> nutrients) {
        if (recipe.getNutritionTags() == null || nutrients.isEmpty()) {
            return true; // 如果没有营养标签信息，默认适合
        }

        String nutritionTags = recipe.getNutritionTags().toLowerCase();
        return nutrients.stream()
                .anyMatch(nutrient -> nutritionTags.contains(nutrient.toLowerCase()));
    }

    /**
     * 基于年龄筛选食谱
     */
    private List<Recipe> filterByAgeGroup(List<Recipe> recipes, int age) {
        String ageGroup = determineAgeGroup(age);
        return recipes.stream()
                .filter(recipe -> recipe.getAgeGroup() != null &&
                        recipe.getAgeGroup().toLowerCase().contains(ageGroup.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * 确定年龄段
     */
    private String determineAgeGroup(int age) {
        if (age <= 2) return "infant";
        if (age <= 5) return "toddler";
        if (age <= 12) return "child";
        if (age <= 18) return "teen";
        return "adult";
    }

    /**
     * 基于饮食偏好推荐食谱
     */
    private List<Recipe> recommendByPreferences(Long childId, Recipe.MealType mealType) {
        // 获取最近30天的饮食记录
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        List<DietaryRecord> recentRecords = dietaryRecordRepository
                .findByChildIdAndRecordDateBetween(childId, startDateTime, endDateTime);

        // 分析常吃的食物类别
        Map<String, Long> foodCategoryFrequency = recentRecords.stream()
                .filter(record -> record.getFoodItem() != null && record.getFoodItem().getCategory() != null)
                .collect(Collectors.groupingBy(
                        record -> record.getFoodItem().getCategory(),
                        Collectors.counting()
                ));

        // 找出最常吃的食物类别
        List<String> preferredCategories = foodCategoryFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 推荐相似类别的食谱
        List<Recipe> allRecipes = recipeRepository.findBySuitableMealTypeAndIsActiveTrue(mealType);
        return allRecipes.stream()
                .filter(recipe -> isFromPreferredCategories(recipe, preferredCategories))
                .collect(Collectors.toList());
    }

    /**
     * 判断食谱是否来自偏好类别
     */
    private boolean isFromPreferredCategories(Recipe recipe, List<String> preferredCategories) {
        if (preferredCategories.isEmpty()) {
            return true;
        }

        // 这里可以根据食谱的食材来判断类别
        // 简化处理：如果食谱名称包含偏好类别关键词，则认为匹配
        String recipeName = recipe.getName().toLowerCase();
        return preferredCategories.stream()
                .anyMatch(category -> recipeName.contains(category.toLowerCase()));
    }

    /**
     * 综合评分和排序
     */
    private List<Recipe> combineAndScoreRecommendations(List<Recipe>... recipeLists) {
        Map<Recipe, Integer> recipeScores = new HashMap<>();

        // 为每个食谱计算得分
        for (List<Recipe> recipeList : recipeLists) {
            for (Recipe recipe : recipeList) {
                recipeScores.merge(recipe, 1, Integer::sum);
            }
        }

        // 按得分排序
        return recipeScores.entrySet().stream()
                .sorted(Map.Entry.<Recipe, Integer>comparingByValue().reversed())
                .limit(10) // 返回前10个推荐
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 生成详细的推荐理由
     */
    private List<Map<String, Object>> generateDetailedRecommendations(
            List<Recipe> recipes, Child child, Map<String, Object> nutritionAnalysis) {

        return recipes.stream().map(recipe -> {
            Map<String, Object> recommendation = new HashMap<>();
            recommendation.put("recipe", recipe);
            recommendation.put("reasons", generateRecommendationReasons(recipe, child, nutritionAnalysis));
            recommendation.put("suitabilityScore", calculateSuitabilityScore(recipe, child, nutritionAnalysis));
            return recommendation;
        }).collect(Collectors.toList());
    }

    /**
     * 生成推荐理由
     */
    private List<String> generateRecommendationReasons(
            Recipe recipe, Child child, Map<String, Object> nutritionAnalysis) {

        List<String> reasons = new ArrayList<>();

        // 基于年龄的推荐
        int age = calculateAge(child.getBirthDate());
        String ageGroup = determineAgeGroup(age);
        if (recipe.getAgeGroup() != null && recipe.getAgeGroup().toLowerCase().contains(ageGroup)) {
            reasons.add("适合" + age + "岁年龄段");
        }

        // 基于ASD特质的推荐
        ASDProfile asdProfile = child.getAsdProfile();
        if (asdProfile != null && isRecipeASDCompatible(recipe, asdProfile)) {
            reasons.add("考虑ASD特质设计");
        }

        // 基于营养需求的推荐
        if (nutritionAnalysis != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> balanceAssessment = (Map<String, Object>) nutritionAnalysis.get("balanceAssessment");
            if (balanceAssessment != null) {
                @SuppressWarnings("unchecked")
                Map<String, String> status = (Map<String, String>) balanceAssessment.get("status");

                List<String> deficientNutrients = status.entrySet().stream()
                        .filter(entry -> "不足".equals(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                if (isRichInNutrients(recipe, deficientNutrients)) {
                    reasons.add("有助于补充缺乏的营养素");
                }
            }
        }

        // 基于烹饪难度的推荐
        if (recipe.getDifficultyLevel() != null && recipe.getDifficultyLevel() <= 2) {
            reasons.add("制作简单，易于准备");
        }

        // 基于烹饪时间的推荐
        if (recipe.getCookingTime() != null && recipe.getCookingTime() <= 30) {
            reasons.add("制作时间短（30分钟内）");
        }

        if (reasons.isEmpty()) {
            reasons.add("综合评估推荐");
        }

        return reasons;
    }

    /**
     * 计算适合度评分
     */
    private int calculateSuitabilityScore(
            Recipe recipe, Child child, Map<String, Object> nutritionAnalysis) {

        int score = 50; // 基础分

        // 年龄匹配加分
        int age = calculateAge(child.getBirthDate());
        String ageGroup = determineAgeGroup(age);
        if (recipe.getAgeGroup() != null && recipe.getAgeGroup().toLowerCase().contains(ageGroup)) {
            score += 20;
        }

        // ASD特质匹配加分
        ASDProfile asdProfile = child.getAsdProfile();
        if (asdProfile != null && isRecipeASDCompatible(recipe, asdProfile)) {
            score += 15;
        }

        // 营养需求匹配加分
        if (nutritionAnalysis != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> balanceAssessment = (Map<String, Object>) nutritionAnalysis.get("balanceAssessment");
            if (balanceAssessment != null) {
                @SuppressWarnings("unchecked")
                Map<String, String> status = (Map<String, String>) balanceAssessment.get("status");

                List<String> deficientNutrients = status.entrySet().stream()
                        .filter(entry -> "不足".equals(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                if (isRichInNutrients(recipe, deficientNutrients)) {
                    score += 15;
                }
            }
        }

        return Math.min(score, 100); // 最高100分
    }

    /**
     * 获取最近的营养分析
     */
    private Map<String, Object> getRecentNutritionAnalysis(Long childId) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(7); // 最近7天
            return nutritionAnalysisService.analyzeNutritionIntake(childId, startDate, endDate);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * 计算年龄
     */
    private int calculateAge(Date birthDate) {
        if (birthDate == null) return 6; // 默认年龄

        LocalDate birth = new java.sql.Date(birthDate.getTime()).toLocalDate();
        LocalDate now = LocalDate.now();
        return (int) java.time.temporal.ChronoUnit.YEARS.between(birth, now);
    }

    /**
     * 获取个性化推荐
     */
    public Map<String, Object> getPersonalizedRecommendations(Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("儿童信息未找到"));

        Map<String, Object> result = new HashMap<>();

        // 为不同餐次生成推荐
        for (Recipe.MealType mealType : Recipe.MealType.values()) {
            if (mealType != Recipe.MealType.ANY) {
                Map<String, Object> recommendations = getSmartRecommendations(childId, mealType);
                result.put(mealType.name().toLowerCase(), recommendations);
            }
        }

        result.put("childName", child.getName());
        result.put("generatedAt", LocalDateTime.now());

        return result;
    }

    /**
     * 获取随机推荐
     */
    public List<Recipe> getRandomRecommendations(int count) {
        return recipeRepository.findRandomRecipes(count);
    }

    /**
     * 搜索食谱
     */
    public List<Recipe> searchRecipes(String keyword, Recipe.MealType mealType,
                                    Integer maxDifficulty, Integer maxCookingTime) {

        // 首先按关键词搜索
        List<Recipe> keywordResults = recipeRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword);

        // 然后按其他条件筛选
        return keywordResults.stream()
                .filter(recipe -> mealType == null || recipe.getSuitableMealType() == mealType)
                .filter(recipe -> maxDifficulty == null ||
                        (recipe.getDifficultyLevel() != null && recipe.getDifficultyLevel() <= maxDifficulty))
                .filter(recipe -> maxCookingTime == null ||
                        (recipe.getCookingTime() != null && recipe.getCookingTime() <= maxCookingTime))
                .collect(Collectors.toList());
    }
}

