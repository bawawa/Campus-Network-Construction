package com.own.asd.repository;

import com.own.asd.model.nutrition.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 新增方法用于营养数据库
    List<FoodItem> findByNameContainingIgnoreCaseAndIsActiveTrue(String keyword, Pageable pageable);

    @Query("SELECT f FROM FoodItem f WHERE LOWER(f.category) = LOWER(:category) AND LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND f.isActive = true")
    List<FoodItem> findByCategoryAndNameContainingIgnoreCaseAndIsActiveTrue(@Param("category") String category, @Param("keyword") String keyword, Pageable pageable);

    List<FoodItem> findByCategoryAndIsActiveTrueOrderByProteinPer100gDesc(FoodItem.FoodCategory category);

    Page<FoodItem> findByIsActiveTrueOrderByProteinPer100gDesc(Pageable pageable);

    Page<FoodItem> findByIsActiveTrueOrderByFiberPer100gDesc(Pageable pageable);

    Page<FoodItem> findByIsActiveTrueOrderByVitaminCPer100gDesc(Pageable pageable);

    Page<FoodItem> findByIsActiveTrueOrderByCalciumPer100gDesc(Pageable pageable);

    long countByIsActiveTrue();

    Page<FoodItem> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT f.category, COUNT(f) FROM FoodItem f WHERE f.isActive = true GROUP BY f.category")
    List<Object[]> countByCategoryAndIsActiveTrue();
}

