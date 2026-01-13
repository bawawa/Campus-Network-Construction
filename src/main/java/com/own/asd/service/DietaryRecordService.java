package com.own.asd.service;

import com.own.asd.model.nutrition.DietaryRecord;
import com.own.asd.model.user.Child;
import com.own.asd.repository.ChildRepository;
import com.own.asd.repository.DietaryRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DietaryRecordService {

    private final DietaryRecordRepository dietaryRecordRepository;
    private final ChildRepository childRepository;

    public DietaryRecord createDietaryRecord(Long childId, DietaryRecord dietaryRecord) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found with id: " + childId));

        dietaryRecord.setChild(child);
        return dietaryRecordRepository.save(dietaryRecord);
    }

    public List<DietaryRecord> getDietaryRecordsByChildAndDate(Long childId, LocalDate date) {
        return dietaryRecordRepository.findByChildIdAndRecordDateOrderByRecordTimeAsc(childId, date);
    }

    public List<DietaryRecord> getDietaryRecordsByChildAndDateRange(
            Long childId, LocalDate startDate, LocalDate endDate) {
        return dietaryRecordRepository.findByChildIdAndRecordDateBetweenOrderByRecordDateAscRecordTimeAsc(
                childId, startDate, endDate);
    }

    public List<DietaryRecord> getDietaryRecordsByChildAndMealType(
            Long childId, LocalDate date, DietaryRecord.MealType mealType) {
        return dietaryRecordRepository.findByChildIdAndDateAndMealType(childId, date, mealType);
    }

    public List<DietaryRecord> getRecentDietaryRecords(Long childId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return dietaryRecordRepository.findRecentRecordsByChild(childId, startDate);
    }

    public DietaryRecord updateDietaryRecord(Long childId, Long recordId, DietaryRecord recordDetails) {
        DietaryRecord existingRecord = dietaryRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Dietary record not found with id: " + recordId));

        if (!existingRecord.getChild().getId().equals(childId)) {
            throw new RuntimeException("Dietary record does not belong to the specified child");
        }

        // Update record fields
        existingRecord.setRecordDate(recordDetails.getRecordDate());
        existingRecord.setRecordTime(recordDetails.getRecordTime());
        existingRecord.setMealType(recordDetails.getMealType());
        existingRecord.setFoodItem(recordDetails.getFoodItem());
        existingRecord.setQuantity(recordDetails.getQuantity());
        existingRecord.setUnit(recordDetails.getUnit());
        existingRecord.setCookingMethod(recordDetails.getCookingMethod());
        existingRecord.setEatingMood(recordDetails.getEatingMood());
        existingRecord.setBehaviorNotes(recordDetails.getBehaviorNotes());
        existingRecord.setNotes(recordDetails.getNotes());

        return dietaryRecordRepository.save(existingRecord);
    }

    public void deleteDietaryRecord(Long childId, Long recordId) {
        DietaryRecord record = dietaryRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Dietary record not found with id: " + recordId));

        if (!record.getChild().getId().equals(childId)) {
            throw new RuntimeException("Dietary record does not belong to the specified child");
        }

        dietaryRecordRepository.delete(record);
    }

    public long getRecordDaysCount(Long childId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return dietaryRecordRepository.countDistinctRecordDatesByChild(childId, startDate);
    }
}

