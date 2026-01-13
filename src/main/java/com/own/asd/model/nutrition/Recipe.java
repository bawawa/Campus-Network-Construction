package com.own.asd.model.nutrition;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipes")
@Data
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "chinese_name", length = 200)
    private String chineseName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients; // JSON格式存储食材清单

    @Column(name = "cooking_steps", columnDefinition = "TEXT")
    private String cookingSteps; // JSON格式存储制作步骤

    @Column(name = "cooking_time")
    private Integer cookingTime; // 分钟

    @Column(name = "difficulty_level")
    private Integer difficultyLevel; // 1-5级难度

    @Enumerated(EnumType.STRING)
    private MealType suitableMealType;

    @Column(name = "age_group", length = 50)
    private String ageGroup; // 适合的年龄段

    @Column(name = "nutrition_tags", columnDefinition = "TEXT")
    private String nutritionTags; // JSON格式存储营养标签

    @Column(name = "texture_notes", columnDefinition = "TEXT")
    private String textureNotes; // 质地说明

    @Column(name = "allergen_warnings", columnDefinition = "TEXT")
    private String allergenWarnings; // 过敏原警告

    @Column(name = "feeding_tips", columnDefinition = "TEXT")
    private String feedingTips; // 喂食小贴士

    @Column(name = "is_preset")
    private Boolean isPreset = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK, SUPPER, ANY
    }
}

