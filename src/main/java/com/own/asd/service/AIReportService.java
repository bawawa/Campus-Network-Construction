package com.own.asd.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class AIReportService {

    @Value("${ai.api.token:sk-bitihjqsmpuhrqwugrbnwtzixuyulxtrcnnacbgqjcistdvn}")
    private String apiToken;

    @Value("${ai.api.url:https://api.siliconflow.cn/v1/messages}")
    private String apiUrl;

    @Value("${ai.api.model:Pro/deepseek-ai/DeepSeek-V3.2}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 使用 AI 生成营养分析报告
     */
    public String generateNutritionReport(Map<String, Object> nutritionData) {
        try {
            String prompt = buildPrompt(nutritionData);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 4096);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            log.info("Sending request to AI API: {}", apiUrl);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return parseAIResponse(response.getBody());
            } else {
                log.error("AI API returned error: {}", response.getStatusCode());
                return "AI报告生成失败，请稍后重试。";
            }
        } catch (Exception e) {
            log.error("Error generating nutrition report", e);
            return "AI报告生成失败: " + e.getMessage();
        }
    }

    /**
     * 构建提示词
     */
    private String buildPrompt(Map<String, Object> data) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请作为一位专业的儿童营养师，根据以下营养分析数据生成一份详细的营养报告。\n\n");
        prompt.append("## 分析周期\n");
        Map<String, Object> period = (Map<String, Object>) data.get("analysisPeriod");
        if (period != null) {
            prompt.append("开始日期: ").append(period.get("startDate")).append("\n");
            prompt.append("结束日期: ").append(period.get("endDate")).append("\n");
        }
        prompt.append("记录总数: ").append(data.get("totalRecords")).append(" 条\n\n");

        prompt.append("## 营养摄入汇总\n");
        @SuppressWarnings("unchecked")
        Map<String, Double> nutritionSummary = (Map<String, Double>) data.get("nutritionSummary");
        if (nutritionSummary != null) {
            prompt.append("热量: ").append(nutritionSummary.get("calories")).append(" kcal\n");
            prompt.append("蛋白质: ").append(nutritionSummary.get("protein")).append(" g\n");
            prompt.append("碳水化合物: ").append(nutritionSummary.get("carbohydrates")).append(" g\n");
            prompt.append("脂肪: ").append(nutritionSummary.get("fat")).append(" g\n");
            prompt.append("膳食纤维: ").append(nutritionSummary.get("fiber")).append(" g\n");
            prompt.append("钙: ").append(nutritionSummary.get("calcium")).append(" mg\n");
            prompt.append("铁: ").append(nutritionSummary.get("iron")).append(" mg\n");
            prompt.append("维生素C: ").append(nutritionSummary.get("vitaminC")).append(" mg\n");
        }

        prompt.append("\n## 营养均衡评估\n");
        @SuppressWarnings("unchecked")
        Map<String, Object> balanceAssessment = (Map<String, Object>) data.get("balanceAssessment");
        if (balanceAssessment != null) {
            @SuppressWarnings("unchecked")
            Map<String, String> status = (Map<String, String>) balanceAssessment.get("status");
            if (status != null) {
                prompt.append("热量: ").append(status.get("calories")).append("\n");
                prompt.append("蛋白质: ").append(status.get("protein")).append("\n");
                prompt.append("碳水化合物: ").append(status.get("carbohydrates")).append("\n");
                prompt.append("脂肪: ").append(status.get("fat")).append("\n");
                prompt.append("膳食纤维: ").append(status.get("fiber")).append("\n");
                prompt.append("钙: ").append(status.get("calcium")).append("\n");
                prompt.append("铁: ").append(status.get("iron")).append("\n");
                prompt.append("维生素C: ").append(status.get("vitaminC")).append("\n");
            }
            prompt.append("\n综合评分: ").append(balanceAssessment.get("overallScore")).append("/100\n");
        }

        prompt.append("\n## 餐次分布\n");
        @SuppressWarnings("unchecked")
        Map<String, Object> mealDistribution = (Map<String, Object>) data.get("mealDistribution");
        if (mealDistribution != null) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> mealCount = (Map<String, Integer>) mealDistribution.get("mealCount");
            if (mealCount != null) {
                prompt.append("用餐总数: ").append(mealDistribution.get("totalMeals")).append("\n");
                mealCount.forEach((meal, count) -> prompt.append(meal).append(": ").append(count).append(" 次\n"));
            }
        }

        prompt.append("\n## 食物类别分析\n");
        @SuppressWarnings("unchecked")
        Map<String, Object> foodCategoryAnalysis = (Map<String, Object>) data.get("foodCategoryAnalysis");
        if (foodCategoryAnalysis != null) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> categoryCount = (Map<String, Integer>) foodCategoryAnalysis.get("categoryCount");
            if (categoryCount != null) {
                prompt.append("食物种类: ").append(foodCategoryAnalysis.get("totalCategories")).append("\n");
                categoryCount.forEach((category, count) -> prompt.append(category).append(": ").append(count).append(" 种\n"));
            }
        }

        prompt.append("\n## 报告要求\n");
        prompt.append("请根据以上数据生成一份专业的营养分析报告，包含以下内容：\n");
        prompt.append("1. 营养摄入概况：总结整体营养摄入情况\n");
        prompt.append("2. 营养状况分析：分析各项营养素的摄入是否达标\n");
        prompt.append("3. 餐次饮食分析：分析三餐加餐的营养分布是否合理\n");
        prompt.append("4. 食物多样性分析：分析食物种类是否丰富\n");
        prompt.append("5. 问题识别：指出当前饮食中存在的问题\n");
        prompt.append("6. 具体建议：给出针对性的营养改善建议\n");
        prompt.append("7. ASD儿童饮食建议：考虑ASD儿童的特殊需求（如有偏食、敏感等），提供实用建议\n");
        prompt.append("8. 下周饮食计划：提供简单的下周饮食参考建议\n\n");
        prompt.append("请用清晰、专业的语言，用中文回复，报告要有条理性和实用性。");

        return prompt.toString();
    }

    /**
     * 解析 AI 响应
     */
    private String parseAIResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode contentNode = root.path("content");

            if (contentNode.isArray() && contentNode.size() > 0) {
                JsonNode firstContent = contentNode.get(0);
                JsonNode textNode = firstContent.path("text");
                return textNode.asText();
            }

            return "AI报告解析失败";
        } catch (Exception e) {
            log.error("Error parsing AI response", e);
            return "AI报告解析失败: " + e.getMessage();
        }
    }
}

