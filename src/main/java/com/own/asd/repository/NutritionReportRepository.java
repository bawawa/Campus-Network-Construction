package com.own.asd.repository;

import com.own.asd.model.nutrition.NutritionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NutritionReportRepository extends JpaRepository<NutritionReport, Long> {

    /**
     * 查找儿童的所有报告
     */
    List<NutritionReport> findByChildIdOrderByGeneratedAtDesc(Long childId);

    /**
     * 查找儿童指定类型的报告
     */
    List<NutritionReport> findByChildIdAndReportTypeOrderByGeneratedAtDesc(Long childId, NutritionReport.ReportType reportType);

    /**
     * 查找儿童在指定日期范围内的报告
     */
    List<NutritionReport> findByChildIdAndReportDateBetweenOrderByGeneratedAtDesc(
            Long childId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * 查找儿童最新的AI报告
     */
    @Query("SELECT r FROM NutritionReport r WHERE r.child.id = :childId AND r.reportType = 'AI' ORDER BY r.generatedAt DESC")
    Optional<NutritionReport> findLatestAIReport(@Param("childId") Long childId);

    /**
     * 检查是否已存在相同日期范围的报告
     */
    @Query("SELECT r FROM NutritionReport r WHERE r.child.id = :childId " +
           "AND r.reportType = :reportType " +
           "AND ((r.startDate <= :endDate AND r.endDate >= :startDate))")
    List<NutritionReport> findOverlappingReports(
            @Param("childId") Long childId,
            @Param("reportType") NutritionReport.ReportType reportType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}

