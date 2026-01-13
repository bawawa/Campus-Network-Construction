package com.own.asd.controller;

import com.own.asd.model.nutrition.DietaryRecord;
import com.own.asd.service.DietaryRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/children/{childId}/dietary-records")
@RequiredArgsConstructor
public class DietaryRecordController {

    private final DietaryRecordService dietaryRecordService;

    @PostMapping
    public ResponseEntity<DietaryRecord> createDietaryRecord(
            @PathVariable Long childId,
            @Valid @RequestBody DietaryRecord dietaryRecord) {
        DietaryRecord createdRecord = dietaryRecordService.createDietaryRecord(childId, dietaryRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRecord);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<DietaryRecord>> getDietaryRecordsByDate(
            @PathVariable Long childId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<DietaryRecord> records = dietaryRecordService.getDietaryRecordsByChildAndDate(childId, date);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<DietaryRecord>> getDietaryRecordsByDateRange(
            @PathVariable Long childId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DietaryRecord> records = dietaryRecordService.getDietaryRecordsByChildAndDateRange(
                childId, startDate, endDate);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/meal-type/{mealType}")
    public ResponseEntity<List<DietaryRecord>> getDietaryRecordsByMealType(
            @PathVariable Long childId,
            @PathVariable DietaryRecord.MealType mealType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<DietaryRecord> records = dietaryRecordService.getDietaryRecordsByChildAndMealType(
                childId, date, mealType);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<DietaryRecord>> getRecentDietaryRecords(
            @PathVariable Long childId,
            @RequestParam(defaultValue = "7") int days) {
        List<DietaryRecord> records = dietaryRecordService.getRecentDietaryRecords(childId, days);
        return ResponseEntity.ok(records);
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<DietaryRecord> updateDietaryRecord(
            @PathVariable Long childId,
            @PathVariable Long recordId,
            @Valid @RequestBody DietaryRecord recordDetails) {
        DietaryRecord updatedRecord = dietaryRecordService.updateDietaryRecord(childId, recordId, recordDetails);
        return ResponseEntity.ok(updatedRecord);
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteDietaryRecord(
            @PathVariable Long childId,
            @PathVariable Long recordId) {
        dietaryRecordService.deleteDietaryRecord(childId, recordId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/record-days")
    public ResponseEntity<Long> getRecordDaysCount(
            @PathVariable Long childId,
            @RequestParam(defaultValue = "30") int days) {
        long count = dietaryRecordService.getRecordDaysCount(childId, days);
        return ResponseEntity.ok(count);
    }
}

