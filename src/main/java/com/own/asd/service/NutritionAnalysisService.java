package com.own.asd.service;

import com.own.asd.model.nutrition.DietaryRecord;
import com.own.asd.model.nutrition.FoodItem;
import com.own.asd.repository.DietaryRecordRepository;
import com.own.asd.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NutritionAnalysisService {

    @Autowired
    private DietaryRecordRepository dietaryRecordRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    /**
     * 获取儿童的营养摄入分析
     */
    public Map<String, Object> analyzeNutritionIntake(Long childId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        List<DietaryRecord> records = dietaryRecordRepository.findByChildIdAndRecordDateBetween(
                childId, startDateTime, endDateTime);

        Map<String, Object> analysis = new HashMap<>();

        // 基础统计
        analysis.put("totalRecords", records.size());
        analysis.put("analysisPeriod", Map.of(
                "startDate", startDate,
                "endDate", endDate
        ));

        // 营养摄入统计
        Map<String, Double> nutritionSummary = calculateNutritionSummary(records);
        analysis.put("nutritionSummary", nutritionSummary);

        // 每日营养摄入趋势
        List<Map<String, Object>> dailyTrends = calculateDailyTrends(records, startDate, endDate);
        analysis.put("dailyTrends", dailyTrends);

        // 餐次分布分析
        Map<String, Object> mealDistribution = analyzeMealDistribution(records);
        analysis.put("mealDistribution", mealDistribution);

        // 食物类别分析
        Map<String, Object> foodCategoryAnalysis = analyzeFoodCategories(records);
        analysis.put("foodCategoryAnalysis", foodCategoryAnalysis);

        // 营养均衡评估
        Map<String, Object> balanceAssessment = assessNutritionBalance(nutritionSummary);
        analysis.put("balanceAssessment", balanceAssessment);

        // 推荐建议
        List<String> recommendations = generateRecommendations(nutritionSummary, balanceAssessment);
        analysis.put("recommendations", recommendations);

        return analysis;
    }

    /**
     * 计算营养摄入汇总
     */
    private Map<String, Double> calculateNutritionSummary(List<DietaryRecord> records) {
        Map<String, Double> summary = new HashMap<>();

        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalFiber = 0;
        double totalCalcium = 0;
        double totalIron = 0;
        double totalVitaminC = 0;
        double totalVitaminD = 0;

        for (DietaryRecord record : records) {
            if (record.getFoodItem() != null) {
                FoodItem food = record.getFoodItem();
                double quantity = record.getQuantity() != null ? record.getQuantity() : 1.0;

                totalCalories += (food.getCalories() != null ? food.getCalories() : 0) * quantity;
                totalProtein += (food.getProtein() != null ? food.getProtein() : 0) * quantity;
                totalCarbs += (food.getCarbohydrates() != null ? food.getCarbohydrates() : 0) * quantity;
                totalFat += (food.getFat() != null ? food.getFat() : 0) * quantity;
                totalFiber += (food.getFiber() != null ? food.getFiber() : 0) * quantity;
                totalCalcium += (food.getCalcium() != null ? food.getCalcium() : 0) * quantity;
                totalIron += (food.getIron() != null ? food.getIron() : 0) * quantity;
                totalVitaminC += (food.getVitaminC() != null ? food.getVitaminC() : 0) * quantity;
                totalVitaminD += (food.getVitaminD() != null ? food.getVitaminD() : 0) * quantity;
            }
        }

        summary.put("calories", round(totalCalories, 2));
        summary.put("protein", round(totalProtein, 2));
        summary.put("carbohydrates", round(totalCarbs, 2));
        summary.put("fat", round(totalFat, 2));
        summary.put("fiber", round(totalFiber, 2));
        summary.put("calcium", round(totalCalcium, 2));
        summary.put("iron", round(totalIron, 2));
        summary.put("vitaminC", round(totalVitaminC, 2));
        summary.put("vitaminD", round(totalVitaminD, 2));

        return summary;
    }

    /**
     * 计算每日营养摄入趋势
     */
    private List<Map<String, Object>> calculateDailyTrends(List<DietaryRecord> records,
                                                          LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Map<String, Double>> dailyNutrition = new TreeMap<>();

        // 初始化所有日期
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dailyNutrition.put(currentDate, new HashMap<>());
            currentDate = currentDate.plusDays(1);
        }

        // 按日期分组记录
        Map<LocalDate, List<DietaryRecord>> recordsByDate = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getRecordDate().toLocalDate()
                ));

        // 计算每日营养
        for (Map.Entry<LocalDate, List<DietaryRecord>> entry : recordsByDate.entrySet()) {
            Map<String, Double> dayNutrition = calculateNutritionSummary(entry.getValue());
            dailyNutrition.put(entry.getKey(), dayNutrition);
        }

        // 转换为前端需要的格式
        List<Map<String, Object>> trends = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<String, Double>> entry : dailyNutrition.entrySet()) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", entry.getKey().toString());
            dayData.put("nutrition", entry.getValue());
            trends.add(dayData);
        }

        return trends;
    }

    /**
     * 分析餐次分布
     */
    private Map<String, Object> analyzeMealDistribution(List<DietaryRecord> records) {
        Map<String, List<DietaryRecord>> meals = records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getMealType() != null ? record.getMealType() : "其他"
                ));

        Map<String, Object> distribution = new HashMap<>();
        Map<String, Double> mealCalories = new HashMap<>();
        Map<String, Integer> mealCount = new HashMap<>();

        for (Map.Entry<String, List<DietaryRecord>> entry : meals.entrySet()) {
            String mealType = entry.getKey();
            List<DietaryRecord> mealRecords = entry.getValue();

            mealCount.put(mealType, mealRecords.size());

            double totalCalories = 0;
            for (DietaryRecord record : mealRecords) {
                if (record.getFoodItem() != null && record.getFoodItem().getCalories() != null) {
                    double quantity = record.getQuantity() != null ? record.getQuantity() : 1.0;
                    totalCalories += record.getFoodItem().getCalories() * quantity;
                }
            }
            mealCalories.put(mealType, round(totalCalories, 2));
        }

        distribution.put("mealCount", mealCount);
        distribution.put("mealCalories", mealCalories);
        distribution.put("totalMeals", records.size());

        return distribution;
    }

    /**
     * 分析食物类别
     */
    private Map<String, Object> analyzeFoodCategories(List<DietaryRecord> records) {
        Map<String, List<DietaryRecord>> categories = new HashMap<>();

        for (DietaryRecord record : records) {
            if (record.getFoodItem() != null) {
                String category = record.getFoodItem().getCategory() != null ?
                        record.getFoodItem().getCategory() : "其他";
                categories.computeIfAbsent(category, k -> new ArrayList<>()).add(record);
            }
        }

        Map<String, Object> analysis = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();
        Map<String, Double> categoryCalories = new HashMap<>();

        for (Map.Entry<String, List<DietaryRecord>> entry : categories.entrySet()) {
            String category = entry.getKey();
            List<DietaryRecord> categoryRecords = entry.getValue();

            categoryCount.put(category, categoryRecords.size());

            double totalCalories = 0;
            for (DietaryRecord record : categoryRecords) {
                if (record.getFoodItem() != null && record.getFoodItem().getCalories() != null) {
                    double quantity = record.getQuantity() != null ? record.getQuantity() : 1.0;
                    totalCalories += record.getFoodItem().getCalories() * quantity;
                }
            }
            categoryCalories.put(category, round(totalCalories, 2));
        }

        analysis.put("categoryCount", categoryCount);
        analysis.put("categoryCalories", categoryCalories);
        analysis.put("totalCategories", categories.size());

        return analysis;
    }

    /**
     * 评估营养均衡性
     */
    private Map<String, Object> assessNutritionBalance(Map<String, Double> nutritionSummary) {
        Map<String, Object> assessment = new HashMap<>();

        // 推荐摄入量（以6-12岁儿童为基准，可根据年龄调整）
        Map<String, Map<String, Double>> recommendations = new HashMap<>();
        recommendations.put("calories", Map.of("min", 1600.0, "max", 2200.0));
        recommendations.put("protein", Map.of("min", 30.0, "max", 55.0));
        recommendations.put("carbohydrates", Map.of("min", 225.0, "max", 325.0));
        recommendations.put("fat", Map.of("min", 44.0, "max", 70.0));
        recommendations.put("fiber", Map.of("min", 20.0, "max", 35.0));
        recommendations.put("calcium", Map.of("min", 800.0, "max", 1300.0));
        recommendations.put("iron", Map.of("min", 8.0, "max", 15.0));
        recommendations.put("vitaminC", Map.of("min", 45.0, "max", 65.0));
        recommendations.put("vitaminD", Map.of("min", 15.0, "max", 25.0));

        Map<String, String> status = new HashMap<>();
        Map<String, Double> percentages = new HashMap<>();

        for (Map.Entry<String, Double> entry : nutritionSummary.entrySet()) {
            String nutrient = entry.getKey();
            Double intake = entry.getValue();

            if (recommendations.containsKey(nutrient)) {
                Map<String, Double> rec = recommendations.get(nutrient);
                double min = rec.get("min");
                double max = rec.get("max");

                double percentage = (intake / ((min + max) / 2)) * 100;
                percentages.put(nutrient, round(percentage, 1));

                if (intake < min) {
                    status.put(nutrient, "不足");
                } else if (intake > max) {
                    status.put(nutrient, "过量");
                } else {
                    status.put(nutrient, "正常");
                }
            }
        }

        assessment.put("status", status);
        assessment.put("percentages", percentages);
        assessment.put("overallScore", calculateOverallScore(status));

        return assessment;
    }

    /**
     * 计算总体评分
     */
    private int calculateOverallScore(Map<String, String> status) {
        int totalNutrients = status.size();
        if (totalNutrients == 0) return 0;

        long normalCount = status.values().stream()
                .filter(s -> "正常".equals(s))
                .count();

        return (int) Math.round((normalCount * 100.0) / totalNutrients);
    }

    /**
     * 生成推荐建议
     */
    private List<String> generateRecommendations(Map<String, Double> nutritionSummary,
                                                Map<String, Object> balanceAssessment) {
        List<String> recommendations = new ArrayList<>();

        @SuppressWarnings("unchecked")
        Map<String, String> status = (Map<String, String>) balanceAssessment.get("status");

        for (Map.Entry<String, String> entry : status.entrySet()) {
            String nutrient = entry.getKey();
            String nutrientStatus = entry.getValue();

            switch (nutrientStatus) {
                case "不足":
                    recommendations.add(getDeficiencyRecommendation(nutrient));
                    break;
                case "过量":
                    recommendations.add(getExcessRecommendation(nutrient));
                    break;
            }
        }

        // 如果没有特别问题，添加一般性建议
        if (recommendations.isEmpty()) {
            recommendations.add("营养摄入均衡，请继续保持良好的饮食习惯。");
            recommendations.add("建议每天摄入5种不同颜色的蔬果，确保营养多样性。");
        }

        return recommendations;
    }

    private String getDeficiencyRecommendation(String nutrient) {
        switch (nutrient) {
            case "calories":
                return "热量摄入不足，建议增加主食和健康脂肪的摄入。";
            case "protein":
                return "蛋白质摄入不足，建议增加鱼、肉、蛋、奶等优质蛋白食物。";
            case "carbohydrates":
                return "碳水化合物摄入不足，建议增加全谷物和薯类食物。";
            case "fat":
                return "脂肪摄入不足，建议适量增加坚果、植物油等健康脂肪。";
            case "fiber":
                return "膳食纤维摄入不足，建议增加蔬菜、水果和全谷物。";
            case "calcium":
                return "钙质摄入不足，建议增加奶制品、豆制品和绿叶蔬菜。";
            case "iron":
                return "铁质摄入不足，建议增加红肉、动物肝脏和深绿色蔬菜。";
            case "vitaminC":
                return "维生素C摄入不足，建议增加新鲜水果和蔬菜。";
            case "vitaminD":
                return "维生素D摄入不足，建议增加鱼类、蛋黄，适当晒太阳。";
            default:
                return nutrient + "摄入不足，建议咨询营养师调整饮食。";
        }
    }

    private String getExcessRecommendation(String nutrient) {
        switch (nutrient) {
            case "calories":
                return "热量摄入过多，建议控制总热量摄入，增加运动量。";
            case "protein":
                return "蛋白质摄入过多，建议适当减少肉类摄入，增加蔬果比例。";
            case "fat":
                return "脂肪摄入过多，建议减少油炸食品和高脂肪食物的摄入。";
            default:
                return nutrient + "摄入过多，建议适当调整饮食结构。";
        }
    }

    /**
     * 四舍五入工具方法
     */
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * 获取营养摄入趋势分析
     */
    public Map<String, Object> getNutritionTrends(Long childId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        return analyzeNutritionIntake(childId, startDate, endDate);
    }

    /**
     * 获取营养摄入周报
     */
    public Map<String, Object> getWeeklyReport(Long childId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // 最近7天

        Map<String, Object> report = analyzeNutritionIntake(childId, startDate, endDate);
        report.put("reportType", "weekly");
        report.put("generatedAt", LocalDateTime.now());

        return report;
    }

    /**
     * 获取营养摄入月报
     */
    public Map<String, Object> getMonthlyReport(Long childId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29); // 最近30天

        Map<String, Object> report = analyzeNutritionIntake(childId, startDate, endDate);
        report.put("reportType", "monthly");
        report.put("generatedAt", LocalDateTime.now());

        return report;
    }
}

