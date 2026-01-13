package com.own.asd.repository;

import com.own.asd.model.nutrition.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByCategory(FoodItem.FoodCategory category);

    List<FoodItem> findByIsPresetTrue();

    List<FoodItem> findByNameContainingIgnoreCaseOrChineseNameContainingIgnoreCase(String nameEn, String nameCn);

    Optional<FoodItem> findByName(String name);

    Optional<FoodItem> findByChineseName(String chineseName);

    @Query("SELECT f FROM FoodItem f WHERE f.isPreset = true AND f.category = :category")
    List<FoodItem> findPresetFoodsByCategory(@Param("category") FoodItem.FoodCategory category);

    @Query("SELECT f FROM FoodItem f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.chineseName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FoodItem> searchFoods(@Param("keyword") String keyword);
}

