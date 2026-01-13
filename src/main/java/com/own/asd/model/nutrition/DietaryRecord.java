package com.own.asd.model.nutrition;

import com.own.asd.model.user.Child;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "dietary_records")
@Data
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class DietaryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "record_time")
    private LocalTime recordTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    @Column(name = "quantity", precision = 8, scale = 2)
    private BigDecimal quantity; // 克或毫升

    @Column(name = "unit", length = 20)
    private String unit; // 单位：克、毫升、份等

    @Enumerated(EnumType.STRING)
    @Column(name = "cooking_method")
    private CookingMethod cookingMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "eating_mood")
    private EatingMood eatingMood;

    @Column(name = "behavior_notes", columnDefinition = "TEXT")
    private String behaviorNotes;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MealType {
        BREAKFAST, LUNCH, DINNER, SNACK, SUPPER
    }

    public enum CookingMethod {
        RAW, BOILED, STEAMED, FRIED, GRILLED, BAKED, STIR_FRIED, SOUP, OTHER
    }

    public enum EatingMood {
        WILLING, RELUCTANT, RESISTANT, EXCITED, NEUTRAL, PICKY
    }
}

