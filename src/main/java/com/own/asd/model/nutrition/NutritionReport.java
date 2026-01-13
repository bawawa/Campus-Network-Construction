package com.own.asd.model.nutrition;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.own.asd.model.user.Child;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 营养分析报告实体
 */
@Entity
@Table(name = "nutrition_reports")
public class NutritionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    @JsonIgnore
    private Child child;

    /**
     * 报告类型: DAILY, WEEKLY, MONTHLY, AI
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    /**
     * 报告开始日期
     */
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 报告结束日期
     */
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * 营养摄入数据(JSON格式)
     */
    @Column(columnDefinition = "TEXT")
    private String nutritionData;

    /**
     * AI生成的报告内容
     */
    @Lob
    private String aiReportContent;

    /**
     * 总体评分
     */
    @Column
    private Integer overallScore;

    /**
     * 推荐建议(JSON格式)
     */
    @Column(columnDefinition = "TEXT")
    private String recommendations;

    /**
     * 报告生成时间
     */
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }

    public enum ReportType {
        DAILY,
        WEEKLY,
        MONTHLY,
        AI
    }

    public NutritionReport() {
    }

    public NutritionReport(Child child, ReportType reportType, LocalDate startDate, LocalDate endDate) {
        this.child = child;
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChildId() {
        return child != null ? child.getId() : null;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getNutritionData() {
        return nutritionData;
    }

    public void setNutritionData(String nutritionData) {
        this.nutritionData = nutritionData;
    }

    public String getAiReportContent() {
        return aiReportContent;
    }

    public void setAiReportContent(String aiReportContent) {
        this.aiReportContent = aiReportContent;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}

