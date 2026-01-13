# 营养报告保存到数据库功能实现

## 问题描述
之前的 AI 营养报告生成功能只是返回报告内容给前端显示,但没有将报告保存到数据库中,导致报告无法持久化存储和后续查询。

## 解决方案

### 1. 创建 NutritionReportService
创建新的 Service 层来处理报告的保存和查询逻辑。

**文件**: `src/main/java/com/own/asd/service/NutritionReportService.java`

**主要功能**:
- `saveAIReport()`: 保存 AI 报告到数据库
  - 检查是否已存在相同日期范围的 AI 报告
  - 如果存在重叠报告,删除旧报告
  - 创建新的报告记录并保存
  - 提取总体评分和推荐建议并保存
- `getReportsByChild()`: 获取儿童的所有报告
- `getReportsByChildAndType()`: 获取儿童指定类型的报告
- `getLatestAIReport()`: 获取儿童最新的 AI 报告
- `deleteReport()`: 删除报告

### 2. 修改 NutritionAnalysisController
更新 Controller 以调用新的 Service 保存报告。

**文件**: `src/main/java/com/own/asd/controller/NutritionAnalysisController.java`

**修改内容**:
- 添加 `NutritionReportService` 依赖注入
- 在 `generateAIReport()` 方法中:
  - 生成 AI 报告后,调用 `nutritionReportService.saveAIReport()` 保存到数据库
  - 返回响应中包含 `reportId` 和 `generatedAt` 字段
- 新增接口 `GET /api/nutrition-analysis/child/{childId}/reports`: 获取儿童的历史营养报告
- 新增接口 `GET /api/nutrition-analysis/child/{childId}/latest-ai-report`: 获取儿童最新的 AI 报告

### 3. 修改前端 Vuex 模块
更新前端状态管理以返回完整的响应数据。

**文件**: `frontend/src/store/modules/nutrition-analysis.js`

**修改内容**:
- 在 `generateAIReport` action 中返回完整的响应对象(包含 `reportId` 和 `generatedAt`),而不仅仅是报告内容

### 4. 修改前端页面
更新营养报告页面,显示保存成功的提示。

**文件**: `frontend/src/views/reports/NutritionReports.vue`

**修改内容**:
- 在 `generateAIReport()` 方法中:
  - 从响应中获取 `response.data`(报告内容)
  - 检查 `response.reportId` 是否存在
  - 如果存在,显示"AI 报告生成并保存成功"的成功提示

## 数据库表结构

使用已有的 `nutrition_reports` 表:

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| child_id | BIGINT | 儿童 ID |
| report_type | ENUM | 报告类型: DAILY, WEEKLY, MONTHLY, AI |
| start_date | DATE | 报告开始日期 |
| end_date | DATE | 报告结束日期 |
| nutrition_data | TEXT | 营养摄入数据(JSON 格式) |
| ai_report_content | TEXT | AI 生成的报告内容 |
| overall_score | INTEGER | 总体评分 |
| recommendations | TEXT | 推荐建议(JSON 格式) |
| generated_at | DATETIME | 报告生成时间 |

## 新增 API 接口

### 1. 生成 AI 营养分析报告(已修改)
```
POST /api/nutrition-analysis/child/{childId}/ai-report
```

**请求参数**:
- `childId`: 儿童 ID(路径参数)
- `startDate`: 开始日期(可选,默认为7天前)
- `endDate`: 结束日期(可选,默认为今天)

**响应数据**:
```json
{
  "success": true,
  "message": "AI报告生成成功",
  "data": "AI 生成的报告内容",
  "analysisData": { ... },
  "reportId": 123,
  "generatedAt": "2026-01-13 16:20:00"
}
```

### 2. 获取儿童的历史营养报告(新增)
```
GET /api/nutrition-analysis/child/{childId}/reports?reportType=AI
```

**请求参数**:
- `childId`: 儿童 ID(路径参数)
- `reportType`: 报告类型(可选,不指定则返回所有类型)

**响应数据**:
```json
{
  "success": true,
  "message": "获取报告列表成功",
  "data": [
    {
      "id": 123,
      "childId": 4,
      "reportType": "AI",
      "startDate": "2026-01-06",
      "endDate": "2026-01-13",
      "aiReportContent": "...",
      "overallScore": 75,
      "generatedAt": "2026-01-13 16:20:00"
    }
  ]
}
```

### 3. 获取儿童最新的 AI 报告(新增)
```
GET /api/nutrition-analysis/child/{childId}/latest-ai-report
```

**请求参数**:
- `childId`: 儿童 ID(路径参数)

**响应数据**:
```json
{
  "success": true,
  "message": "获取最新 AI 报告成功",
  "data": {
    "id": 123,
    "childId": 4,
    "reportType": "AI",
    "startDate": "2026-01-06",
    "endDate": "2026-01-13",
    "aiReportContent": "...",
    "overallScore": 75,
    "generatedAt": "2026-01-13 16:20:00"
  }
}
```

## 功能特性

1. **自动去重**: 如果在相同日期范围内生成新的 AI 报告,会自动删除旧报告,避免数据冗余。

2. **完整信息保存**: 不仅保存 AI 报告内容,还保存:
   - 营养分析数据(nutrition_data)
   - 总体评分(overall_score)
   - 推荐建议(recommendations)
   - 日期范围和生成时间

3. **前端提示**: 生成报告成功后,前端会显示"AI 报告生成并保存成功"的提示消息,让用户知道报告已保存。

4. **历史查询**: 支持查询历史报告列表,可以按报告类型筛选。

## 使用示例

### 1. 生成 AI 报告(自动保存)
```javascript
// 前端调用
const response = await this.$store.dispatch('nutritionAnalysis/generateAIReport', {
  childId: 4,
  startDate: '2026-01-06',
  endDate: '2026-01-13'
})

// 响应包含 reportId
if (response.reportId) {
  console.log('报告已保存, ID:', response.reportId)
}
```

### 2. 查询历史报告
```javascript
// 查询所有报告
const allReports = await axios.get('/api/nutrition-analysis/child/4/reports')

// 查询 AI 报告
const aiReports = await axios.get('/api/nutrition-analysis/child/4/reports?reportType=AI')
```

### 3. 查询最新 AI 报告
```javascript
const latestReport = await axios.get('/api/nutrition-analysis/child/4/latest-ai-report')
```

## 测试验证

1. ✅ 后端编译成功
2. ✅ 创建了 NutritionReportService
3. ✅ 修改了 NutritionAnalysisController
4. ✅ 修改了前端 Vuex 模块
5. ✅ 修改了前端页面
6. ⏳ 需要测试实际功能

## 后续优化建议

1. **报告列表页面**: 可以创建一个专门的历史报告列表页面,展示所有已保存的报告。

2. **报告对比功能**: 支持选择多个报告进行对比,查看营养状况的变化趋势。

3. **报告分享功能**: 支持将报告分享给营养师或其他家长。

4. **报告导出**: 支持将报告导出为 PDF 或 Word 文档。

5. **定时报告生成**: 可以设置定时任务,定期自动生成周报和月报。

