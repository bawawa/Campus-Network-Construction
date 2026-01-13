package com.own.asd.controller;

import com.own.asd.service.NutritionAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/nutrition-analysis")
public class NutritionAnalysisController {

    @Autowired
    private NutritionAnalysisService nutritionAnalysisService;

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
}

