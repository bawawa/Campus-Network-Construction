package com.own.asd.model.nutrition;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DietaryRecordDTO {
    private Long foodItemId;
    private DietaryRecord.MealType mealType;
    private BigDecimal quantity;
    private String unit;
    private LocalDate recordDate;
    private LocalTime recordTime;
    private DietaryRecord.CookingMethod cookingMethod;
    private DietaryRecord.EatingMood eatingMood;
    private String behaviorNotes;
    private String notes;
}

