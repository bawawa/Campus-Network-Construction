package com.own.asd.controller;

import com.own.asd.model.nutrition.NutritionReport;
import com.own.asd.service.AIReportService;
import com.own.asd.service.NutritionAnalysisService;
import com.own.asd.service.NutritionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nutrition-analysis")
public class NutritionAnalysisController {

    @Autowired
    private NutritionAnalysisService nutritionAnalysisService;

    @Autowired
    private AIReportService aiReportService;

    @Autowired
    private NutritionReportService nutritionReportService;

    /**
     * 获取营养摄入分析
     */
    @GetMapping("/child/{childId}/analyze")
    public ResponseEntity<Map<String, Object>> analyzeNutritionIntake(
            @PathVariable Long childId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            Map<String, Object> analysis = nutritionAnalysisService.analyzeNutritionIntake(
                    childId, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "营养分析完成");
            response.put("data", analysis);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "营养分析失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取营养摄入趋势
     */
    @GetMapping("/child/{childId}/trends")
    public ResponseEntity<Map<String, Object>> getNutritionTrends(
            @PathVariable Long childId,
            @RequestParam(defaultValue = "7") int days) {

        try {
            Map<String, Object> trends = nutritionAnalysisService.getNutritionTrends(childId, days);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "趋势分析完成");
            response.put("data", trends);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "趋势分析失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取周报
     */
    @GetMapping("/child/{childId}/weekly-report")
    public ResponseEntity<Map<String, Object>> getWeeklyReport(@PathVariable Long childId) {
        try {
            Map<String, Object> report = nutritionAnalysisService.getWeeklyReport(childId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "周报生成成功");
            response.put("data", report);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "周报生成失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取月报
     */
    @GetMapping("/child/{childId}/monthly-report")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(@PathVariable Long childId) {
        try {
            Map<String, Object> report = nutritionAnalysisService.getMonthlyReport(childId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "月报生成成功");
            response.put("data", report);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "月报生成失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取快速分析报告（最近7天）
     */
    @GetMapping("/child/{childId}/quick-analysis")
    public ResponseEntity<Map<String, Object>> getQuickAnalysis(@PathVariable Long childId) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6); // 最近7天

            Map<String, Object> analysis = nutritionAnalysisService.analyzeNutritionIntake(
                    childId, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "快速分析完成");
            response.put("data", analysis);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "快速分析失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 生成 AI 营养分析报告
     */
    @PostMapping("/child/{childId}/ai-report")
    public ResponseEntity<Map<String, Object>> generateAIReport(
            @PathVariable Long childId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // 如果没有指定日期范围，使用最近7天
            if (startDate == null || endDate == null) {
                endDate = LocalDate.now();
                startDate = endDate.minusDays(6);
            }

            // 获取营养分析数据
            Map<String, Object> nutritionData = nutritionAnalysisService.analyzeNutritionIntake(
                    childId, startDate, endDate);

            // 使用 AI 生成报告
            String aiReport = aiReportService.generateNutritionReport(nutritionData);

            // 保存报告到数据库
            NutritionReport savedReport = nutritionReportService.saveAIReport(
                    childId, aiReport, nutritionData, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "AI报告生成成功");
            response.put("data", aiReport);
            response.put("analysisData", nutritionData);
            response.put("reportId", savedReport.getId());
            response.put("generatedAt", savedReport.getGeneratedAt());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "AI报告生成失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取儿童的历史营养报告
     */
    @GetMapping("/child/{childId}/reports")
    public ResponseEntity<Map<String, Object>> getChildReports(
            @PathVariable Long childId,
            @RequestParam(required = false) String reportType) {
        try {
            List<NutritionReport> reports;

            if (reportType != null && !reportType.isEmpty()) {
                NutritionReport.ReportType type = NutritionReport.ReportType.valueOf(reportType.toUpperCase());
                reports = nutritionReportService.getReportsByChildAndType(childId, type);
            } else {
                reports = nutritionReportService.getReportsByChild(childId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取报告列表成功");
            response.put("data", reports);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取报告列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取儿童最新的 AI 报告
     */
    @GetMapping("/child/{childId}/latest-ai-report")
    public ResponseEntity<Map<String, Object>> getLatestAIReport(@PathVariable Long childId) {
        try {
            return nutritionReportService.getLatestAIReport(childId)
                    .map(report -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "获取最新 AI 报告成功");
                        response.put("data", report);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("message", "未找到 AI 报告");
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取 AI 报告失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

