package com.own.asd.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.own.asd.model.nutrition.NutritionReport;
import com.own.asd.model.user.Child;
import com.own.asd.repository.ChildRepository;
import com.own.asd.repository.NutritionReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class NutritionReportService {

    @Autowired
    private NutritionReportRepository nutritionReportRepository;

    @Autowired
    private ChildRepository childRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 保存 AI 报告到数据库
     */
    public NutritionReport saveAIReport(
            Long childId,
            String aiReportContent,
            Map<String, Object> nutritionData,
            LocalDate startDate,
            LocalDate endDate) {
        try {
            // 获取 Child 对象
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new RuntimeException("儿童不存在: " + childId));

            // 检查是否已存在相同日期范围的 AI 报告
            List<NutritionReport> overlappingReports = nutritionReportRepository.findOverlappingReports(
                    childId,
                    NutritionReport.ReportType.AI,
                    startDate,
                    endDate
            );

            // 如果存在重叠报告，删除旧报告
            if (!overlappingReports.isEmpty()) {
                log.info("Found {} overlapping AI reports, deleting them", overlappingReports.size());
                nutritionReportRepository.deleteAll(overlappingReports);
            }

            // 创建新的报告
            NutritionReport report = new NutritionReport();
            report.setChild(child);
            report.setReportType(NutritionReport.ReportType.AI);
            report.setStartDate(startDate);
            report.setEndDate(endDate);
            report.setAiReportContent(aiReportContent);

            // 保存营养数据为 JSON
            if (nutritionData != null) {
                report.setNutritionData(objectMapper.writeValueAsString(nutritionData));

                // 提取总体评分
                @SuppressWarnings("unchecked")
                Map<String, Object> balanceAssessment = (Map<String, Object>) nutritionData.get("balanceAssessment");
                if (balanceAssessment != null) {
                    Object overallScore = balanceAssessment.get("overallScore");
                    if (overallScore instanceof Integer) {
                        report.setOverallScore((Integer) overallScore);
                    } else if (overallScore instanceof Double) {
                        report.setOverallScore(((Double) overallScore).intValue());
                    }
                }

                // 保存推荐建议
                @SuppressWarnings("unchecked")
                List<String> recommendations = (List<String>) nutritionData.get("recommendations");
                if (recommendations != null) {
                    report.setRecommendations(objectMapper.writeValueAsString(recommendations));
                }
            }

            NutritionReport savedReport = nutritionReportRepository.save(report);
            log.info("Saved AI report for child {} with id {}", childId, savedReport.getId());

            return savedReport;
        } catch (Exception e) {
            log.error("Error saving AI report for child {}", childId, e);
            throw new RuntimeException("保存 AI 报告失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取儿童的所有报告
     */
    public List<NutritionReport> getReportsByChild(Long childId) {
        return nutritionReportRepository.findByChildIdOrderByGeneratedAtDesc(childId);
    }

    /**
     * 获取儿童指定类型的报告
     */
    public List<NutritionReport> getReportsByChildAndType(Long childId, NutritionReport.ReportType reportType) {
        return nutritionReportRepository.findByChildIdAndReportTypeOrderByGeneratedAtDesc(childId, reportType);
    }

    /**
     * 获取儿童最新的 AI 报告
     */
    public Optional<NutritionReport> getLatestAIReport(Long childId) {
        return nutritionReportRepository.findLatestAIReport(childId);
    }

    /**
     * 删除报告
     */
    public void deleteReport(Long reportId) {
        nutritionReportRepository.deleteById(reportId);
    }
}

