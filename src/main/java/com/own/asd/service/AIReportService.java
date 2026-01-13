package com.own.asd.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AIReportService {

    @Value("${ai.api.token:sk-bitihjqsmpuhrqwugrbnwtzixuyulxtrcnnacbgqjcistdvn}")
    private String apiToken;

    @Value("${ai.api.url:https://api.siliconflow.cn/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.api.model:Pro/zai-org/GLM-4.7}")
    private String model;

    @Autowired
    private RestTemplate restTemplate;

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
            requestBody.put("max_tokens", 2048);
            requestBody.put("temperature", 0.7);

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
        prompt.append("作为儿童营养师，请根据以下营养数据生成简明营养报告：\n\n");

        // 核心数据摘要
        @SuppressWarnings("unchecked")
        Map<String, Double> nutritionSummary = (Map<String, Double>) data.get("nutritionSummary");
        @SuppressWarnings("unchecked")
        Map<String, Object> balanceAssessment = (Map<String, Object>) data.get("balanceAssessment");

        prompt.append("【营养数据】\n");
        if (nutritionSummary != null) {
            prompt.append(String.format("热量%.0fkcal, 蛋白质%.1fg, 碳水%.1fg, 脂肪%.1fg\n",
                    nutritionSummary.get("calories"),
                    nutritionSummary.get("protein"),
                    nutritionSummary.get("carbohydrates"),
                    nutritionSummary.get("fat")));
        }
        if (balanceAssessment != null) {
            prompt.append("综合评分: ").append(balanceAssessment.get("overallScore")).append("/100\n");
        }

        // 只列出关键问题
        @SuppressWarnings("unchecked")
        Map<String, String> status = balanceAssessment != null ? (Map<String, String>) balanceAssessment.get("status") : null;
        if (status != null) {
            List<String> issues = new ArrayList<>();
            if ("不足".equals(status.get("protein"))) issues.add("蛋白质不足");
            if ("不足".equals(status.get("calcium"))) issues.add("钙不足");
            if ("不足".equals(status.get("fiber"))) issues.add("膳食纤维不足");
            if ("过量".equals(status.get("calories"))) issues.add("热量超标");
            if (!issues.isEmpty()) {
                prompt.append("主要问题: ").append(String.join(", ", issues)).append("\n");
            }
        }

        prompt.append("\n【报告要求】\n");
        prompt.append("用中文生成以下内容（简洁明了，500字以内）：\n");
        prompt.append("1. 营养状况一句话评价\n");
        prompt.append("2. 3条具体改善建议\n");
        prompt.append("3. ASD儿童饮食注意事项\n");

        return prompt.toString();
    }

    /**
     * 解析 AI 响应
     */
    private String parseAIResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choicesNode = root.path("choices");

            if (choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode firstChoice = choicesNode.get(0);
                JsonNode messageNode = firstChoice.path("message");
                JsonNode contentNode = messageNode.path("content");
                return contentNode.asText();
            }

            return "AI报告解析失败";
        } catch (Exception e) {
            log.error("Error parsing AI response", e);
            return "AI报告解析失败: " + e.getMessage();
        }
    }

    /**
     * 生成自定义推荐（通用方法）
     */
    public String generateCustomRecommendation(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 2048);
            requestBody.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            log.info("Sending custom recommendation request to AI API");

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
                return "AI推荐生成失败，请稍后重试。";
            }
        } catch (Exception e) {
            log.error("Error generating custom recommendation", e);
            return "AI推荐生成失败: " + e.getMessage();
        }
    }
}

