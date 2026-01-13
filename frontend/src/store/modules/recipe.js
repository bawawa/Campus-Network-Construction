import {
  getSmartRecommendations,
  getPersonalizedRecommendations,
  getRandomRecommendations,
  searchRecipes,
  getRecommendationStats
} from '@/api/recipe'

const state = {
  smartRecommendations: [],
  personalizedRecommendations: {},
  randomRecommendations: [],
  searchResults: [],
  recommendationStats: null,
  loading: false,
  selectedChildId: null,
  searchKeyword: '',
  filters: {
    mealType: null,
    maxDifficulty: null,
    maxCookingTime: null
  }
}

const mutations = {
  SET_SMART_RECOMMENDATIONS(state, recommendations) {
    state.smartRecommendations = recommendations
  },
  SET_PERSONALIZED_RECOMMENDATIONS(state, recommendations) {
    state.personalizedRecommendations = recommendations
  },
  SET_RANDOM_RECOMMENDATIONS(state, recommendations) {
    state.randomRecommendations = recommendations
  },
  SET_SEARCH_RESULTS(state, results) {
    state.searchResults = results
  },
  SET_RECOMMENDATION_STATS(state, stats) {
    state.recommendationStats = stats
  },
  SET_LOADING(state, loading) {
    state.loading = loading
  },
  SET_SELECTED_CHILD_ID(state, childId) {
    state.selectedChildId = childId
  },
  SET_SEARCH_KEYWORD(state, keyword) {
    state.searchKeyword = keyword
  },
  SET_FILTERS(state, filters) {
    state.filters = { ...state.filters, ...filters }
  },
  CLEAR_RECOMMENDATIONS(state) {
    state.smartRecommendations = []
    state.personalizedRecommendations = {}
    state.randomRecommendations = []
    state.searchResults = []
  }
}

const actions = {
  // 获取智能推荐
  async getSmartRecommendations({ commit }, { childId, mealType = null }) {
    try {
      commit('SET_LOADING', true)
      commit('SET_SELECTED_CHILD_ID', childId)

      const response = await getSmartRecommendations(childId, mealType)
      if (response.data.success) {
        commit('SET_SMART_RECOMMENDATIONS', response.data.data.recommendations || [])
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

  // 获取个性化推荐
  async getPersonalizedRecommendations({ commit }, childId) {
    try {
      commit('SET_LOADING', true)
      commit('SET_SELECTED_CHILD_ID', childId)

      const response = await getPersonalizedRecommendations(childId)
      if (response.data.success) {
        commit('SET_PERSONALIZED_RECOMMENDATIONS', response.data.data)
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

  // 获取随机推荐
  async getRandomRecommendations({ commit }, count = 5) {
    try {
      commit('SET_LOADING', true)
      const response = await getRandomRecommendations(count)
      if (response.data.success) {
        commit('SET_RANDOM_RECOMMENDATIONS', response.data.data || [])
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

  // 搜索食谱
  async searchRecipes({ commit }, { keyword, mealType = null, maxDifficulty = null, maxCookingTime = null }) {
    try {
      commit('SET_LOADING', true)
      commit('SET_SEARCH_KEYWORD', keyword)

      const response = await searchRecipes(keyword, mealType, maxDifficulty, maxCookingTime)
      if (response.data.success) {
        commit('SET_SEARCH_RESULTS', response.data.data || [])
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

  // 获取推荐统计
  async getRecommendationStats({ commit }, childId) {
    try {
      const response = await getRecommendationStats(childId)
      if (response.data.success) {
        commit('SET_RECOMMENDATION_STATS', response.data.data)
        return response.data
      } else {
        throw new Error(response.data.message)
      }
    } catch (error) {
      throw error
    }
  },

  // 设置选中的儿童
  setSelectedChild({ commit }, childId) {
    commit('SET_SELECTED_CHILD_ID', childId)
  },

  // 设置搜索关键词
  setSearchKeyword({ commit }, keyword) {
    commit('SET_SEARCH_KEYWORD', keyword)
  },

  // 设置筛选条件
  setFilters({ commit }, filters) {
    commit('SET_FILTERS', filters)
  },

  // 清除推荐数据
  clearRecommendations({ commit }) {
    commit('CLEAR_RECOMMENDATIONS')
  }
}

const getters = {
  smartRecommendations: state => state.smartRecommendations,
  personalizedRecommendations: state => state.personalizedRecommendations,
  randomRecommendations: state => state.randomRecommendations,
  searchResults: state => state.searchResults,
  recommendationStats: state => state.recommendationStats,
  loading: state => state.loading,
  selectedChildId: state => state.selectedChildId,
  searchKeyword: state => state.searchKeyword,
  filters: state => state.filters,

  // 按餐次分组的个性化推荐
  breakfastRecommendations: state => state.personalizedRecommendations.breakfast?.recommendations || [],
  lunchRecommendations: state => state.personalizedRecommendations.lunch?.recommendations || [],
  dinnerRecommendations: state => state.personalizedRecommendations.dinner?.recommendations || [],
  snackRecommendations: state => state.personalizedRecommendations.snack?.recommendations || [],

  // 总推荐数量
  totalRecommendations: state => {
    const personalized = state.personalizedRecommendations
    return (personalized.breakfast?.totalCount || 0) +
           (personalized.lunch?.totalCount || 0) +
           (personalized.dinner?.totalCount || 0) +
           (personalized.snack?.totalCount || 0)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

