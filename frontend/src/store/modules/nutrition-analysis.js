import {
  analyzeNutritionIntake,
  getNutritionTrends,
  getWeeklyReport,
  getMonthlyReport,
  getQuickAnalysis
} from '@/api/nutrition-analysis'

const state = {
  currentAnalysis: null,
  trends: [],
  weeklyReport: null,
  monthlyReport: null,
  loading: false,
  selectedChildId: null,
  dateRange: {
    startDate: null,
    endDate: null
  }
}

const mutations = {
  SET_CURRENT_ANALYSIS(state, analysis) {
    state.currentAnalysis = analysis
  },
  SET_TRENDS(state, trends) {
    state.trends = trends
  },
  SET_WEEKLY_REPORT(state, report) {
    state.weeklyReport = report
  },
  SET_MONTHLY_REPORT(state, report) {
    state.monthlyReport = report
  },
  SET_LOADING(state, loading) {
    state.loading = loading
  },
  SET_SELECTED_CHILD_ID(state, childId) {
    state.selectedChildId = childId
  },
  SET_DATE_RANGE(state, dateRange) {
    state.dateRange = dateRange
  },
  CLEAR_ANALYSIS(state) {
    state.currentAnalysis = null
    state.trends = []
    state.weeklyReport = null
    state.monthlyReport = null
  }
}

const actions = {
  // 获取营养摄入分析
  async analyzeNutritionIntake({ commit }, { childId, startDate, endDate }) {
    try {
      commit('SET_LOADING', true)
      commit('SET_SELECTED_CHILD_ID', childId)
      commit('SET_DATE_RANGE', { startDate, endDate })

      const response = await analyzeNutritionIntake(childId, startDate, endDate)
      if (response.data.success) {
        commit('SET_CURRENT_ANALYSIS', response.data.data)
        return response.data
      } else {
        throw new Error(response.data.message)
      }
    } catch (error) {
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 获取营养摄入趋势
  async getNutritionTrends({ commit }, { childId, days = 7 }) {
    try {
      commit('SET_LOADING', true)
      const response = await getNutritionTrends(childId, days)
      if (response.data.success) {
        commit('SET_TRENDS', response.data.data)
        return response.data
      } else {
        throw new Error(response.data.message)
      }
    } catch (error) {
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 获取周报
  async getWeeklyReport({ commit }, childId) {
    try {
      commit('SET_LOADING', true)
      const response = await getWeeklyReport(childId)
      if (response.data.success) {
        commit('SET_WEEKLY_REPORT', response.data.data)
        return response.data
      } else {
        throw new Error(response.data.message)
      }
    } catch (error) {
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 获取月报
  async getMonthlyReport({ commit }, childId) {
    try {
      commit('SET_LOADING', true)
      const response = await getMonthlyReport(childId)
      if (response.data.success) {
        commit('SET_MONTHLY_REPORT', response.data.data)
        return response.data
      } else {
        throw new Error(response.data.message)
      }
    } catch (error) {
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 获取快速分析
  async getQuickAnalysis({ commit }, childId) {
    try {
      commit('SET_LOADING', true)
      const response = await getQuickAnalysis(childId)
      if (response.data.success) {
        commit('SET_CURRENT_ANALYSIS', response.data.data)
        return response.data
      } else {
        throw new Error(response.data.message)
      }
    } catch (error) {
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 设置选中的儿童
  setSelectedChild({ commit }, childId) {
    commit('SET_SELECTED_CHILD_ID', childId)
  },

  // 设置日期范围
  setDateRange({ commit }, dateRange) {
    commit('SET_DATE_RANGE', dateRange)
  },

  // 清除分析数据
  clearAnalysis({ commit }) {
    commit('CLEAR_ANALYSIS')
  }
}

const getters = {
  currentAnalysis: state => state.currentAnalysis,
  trends: state => state.trends,
  weeklyReport: state => state.weeklyReport,
  monthlyReport: state => state.monthlyReport,
  loading: state => state.loading,
  selectedChildId: state => state.selectedChildId,
  dateRange: state => state.dateRange,

  // 营养汇总数据
  nutritionSummary: state => state.currentAnalysis?.nutritionSummary || {},

  // 营养均衡评估
  balanceAssessment: state => state.currentAnalysis?.balanceAssessment || {},

  // 推荐建议
  recommendations: state => state.currentAnalysis?.recommendations || [],

  // 每日趋势数据
  dailyTrends: state => state.currentAnalysis?.dailyTrends || [],

  // 餐次分布
  mealDistribution: state => state.currentAnalysis?.mealDistribution || {},

  // 食物类别分析
  foodCategoryAnalysis: state => state.currentAnalysis?.foodCategoryAnalysis || {},

  // 总体评分
  overallScore: state => state.currentAnalysis?.balanceAssessment?.overallScore || 0
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

