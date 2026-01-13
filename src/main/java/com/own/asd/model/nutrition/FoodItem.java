package com.own.asd.model.nutrition;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_items")
@Data
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Column(name = "chinese_name", length = 200)
    private String chineseName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodCategory category;

    @Column(name = "energy_per_100g", precision = 8, scale = 2)
    private BigDecimal energyPer100g; // 千卡

    @Column(name = "protein_per_100g", precision = 8, scale = 2)
    private BigDecimal proteinPer100g; // 克

    @Column(name = "fat_per_100g", precision = 8, scale = 2)
    private BigDecimal fatPer100g; // 克

    @Column(name = "carbohydrate_per_100g", precision = 8, scale = 2)
    private BigDecimal carbohydratePer100g; // 克

    @Column(name = "fiber_per_100g", precision = 8, scale = 2)
    private BigDecimal fiberPer100g; // 克

    @Column(name = "calcium_per_100g", precision = 8, scale = 2)
    private BigDecimal calciumPer100g; // 毫克

    @Column(name = "iron_per_100g", precision = 8, scale = 2)
    private BigDecimal ironPer100g; // 毫克

    @Column(name = "vitamin_a_per_100g", precision = 8, scale = 2)
    private BigDecimal vitaminAPer100g; // 微克

    @Column(name = "vitamin_c_per_100g", precision = 8, scale = 2)
    private BigDecimal vitaminCPer100g; // 毫克

    @Column(name = "is_preset")
    private Boolean isPreset = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "cooking_methods", columnDefinition = "TEXT")
    private String cookingMethods;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum FoodCategory {
        GRAIN, MEAT, DAIRY, VEGETABLE, FRUIT, SNACK, BEVERAGE, SEAFOOD, LEGUME, NUT, OTHER
    }
}

