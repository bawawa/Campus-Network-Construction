package com.own.asd.repository;

import com.own.asd.model.nutrition.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * 根据餐次类型查找食谱
     */
    List<Recipe> findBySuitableMealTypeAndIsActiveTrue(Recipe.MealType mealType);

    /**
     * 根据年龄段查找食谱
     */
    List<Recipe> findByAgeGroupContainingAndIsActiveTrue(String ageGroup);

    /**
     * 根据难度级别查找食谱
     */
    List<Recipe> findByDifficultyLevelLessThanEqualAndIsActiveTrue(Integer difficultyLevel);

    /**
     * 根据名称搜索食谱
     */
    List<Recipe> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);

    /**
     * 分页查找所有活跃食谱
     */
    Page<Recipe> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 根据营养标签查找食谱
     */
    @Query("SELECT r FROM Recipe r WHERE r.isActive = true AND r.nutritionTags LIKE %:tag%")
    List<Recipe> findByNutritionTag(@Param("tag") String tag);

    /**
     * 根据多个条件组合查找食谱
     */
    @Query("SELECT r FROM Recipe r WHERE " +
           "r.isActive = true AND " +
           "(:mealType IS NULL OR r.suitableMealType = :mealType) AND " +
           "(:maxDifficulty IS NULL OR r.difficultyLevel <= :maxDifficulty) AND " +
           "(:maxCookingTime IS NULL OR r.cookingTime <= :maxCookingTime) AND " +
           "(:ageGroup IS NULL OR r.ageGroup LIKE %:ageGroup%)")
    List<Recipe> findRecipesByCriteria(
            @Param("mealType") Recipe.MealType mealType,
            @Param("maxDifficulty") Integer maxDifficulty,
            @Param("maxCookingTime") Integer maxCookingTime,
            @Param("ageGroup") String ageGroup
    );

    /**
     * 查找预设食谱
     */
    List<Recipe> findByIsPresetTrueAndIsActiveTrue();

    /**
     * 根据烹饪时间范围查找食谱
     */
    List<Recipe> findByCookingTimeBetweenAndIsActiveTrue(Integer minTime, Integer maxTime);

    /**
     * 随机获取指定数量的食谱
     */
    @Query(value = "SELECT * FROM recipes WHERE is_active = true ORDER BY RAND() LIMIT :limit",
           nativeQuery = true)
    List<Recipe> findRandomRecipes(@Param("limit") int limit);

    /**
     * 根据食材查找食谱
     */
    @Query("SELECT r FROM Recipe r WHERE r.isActive = true AND r.ingredients LIKE %:ingredient%")
    List<Recipe> findByIngredient(@Param("ingredient") String ingredient);
}

