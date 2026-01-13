package com.own.asd.repository;

import com.own.asd.model.nutrition.DietaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DietaryRecordRepository extends JpaRepository<DietaryRecord, Long> {

    List<DietaryRecord> findByChildIdAndRecordDateOrderByRecordTimeAsc(Long childId, LocalDate date);

    List<DietaryRecord> findByChildIdAndRecordDateBetweenOrderByRecordDateAscRecordTimeAsc(
            Long childId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT dr FROM DietaryRecord dr WHERE dr.child.id = :childId AND dr.recordDate = :date AND dr.mealType = :mealType")
    List<DietaryRecord> findByChildIdAndDateAndMealType(
            @Param("childId") Long childId,
            @Param("date") LocalDate date,
            @Param("mealType") DietaryRecord.MealType mealType);

    @Query("SELECT dr FROM DietaryRecord dr WHERE dr.child.id = :childId AND dr.recordDate >= :startDate ORDER BY dr.recordDate DESC, dr.recordTime DESC")
    List<DietaryRecord> findRecentRecordsByChild(@Param("childId") Long childId, @Param("startDate") LocalDate startDate);

    @Query("SELECT COUNT(DISTINCT dr.recordDate) FROM DietaryRecord dr WHERE dr.child.id = :childId AND dr.recordDate >= :startDate")
    Long countDistinctRecordDatesByChild(@Param("childId") Long childId, @Param("startDate") LocalDate startDate);

    @Query("SELECT dr FROM DietaryRecord dr WHERE dr.child.id = :childId AND dr.recordDate >= :startDateTime AND dr.recordDate <= :endDateTime ORDER BY dr.recordDate ASC, dr.recordTime ASC")
    List<DietaryRecord> findByChildIdAndRecordDateBetween(@Param("childId") Long childId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
}

