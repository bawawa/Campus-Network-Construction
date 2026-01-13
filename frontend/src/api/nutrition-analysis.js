import request from '@/utils/axios'

/**
 * 营养分析相关API
 */

// 获取营养摄入分析
export function analyzeNutritionIntake(childId, startDate, endDate) {
  return request({
    url: `/nutrition-analysis/child/${childId}/analyze`,
    method: 'get',
    params: {
      startDate,
      endDate
    }
  })
}

// 获取营养摄入趋势
export function getNutritionTrends(childId, days = 7) {
  return request({
    url: `/nutrition-analysis/child/${childId}/trends`,
    method: 'get',
    params: { days }
  })
}

// 获取周报
export function getWeeklyReport(childId) {
  return request({
    url: `/nutrition-analysis/child/${childId}/weekly-report`,
    method: 'get'
  })
}

// 获取月报
export function getMonthlyReport(childId) {
  return request({
    url: `/nutrition-analysis/child/${childId}/monthly-report`,
    method: 'get'
  })
}

// 获取快速分析报告（最近7天）
export function getQuickAnalysis(childId) {
  return request({
    url: `/nutrition-analysis/child/${childId}/quick-analysis`,
    method: 'get'
  })
}

