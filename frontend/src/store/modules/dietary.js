import {
  getDietaryRecordsByChild,
  createDietaryRecord,
  updateDietaryRecord,
  deleteDietaryRecord,
  getFoodItems,
  createFoodItem,
  getNutritionistNotes,
  createNutritionistNote
} from '@/api/dietary'

const state = {
  dietaryRecords: [],
  foodItems: [],
  nutritionistNotes: [],
  currentRecord: null,
  loading: false
}

const mutations = {
  SET_DIETARY_RECORDS(state, records) {
    state.dietaryRecords = records
  },
  SET_FOOD_ITEMS(state, items) {
    state.foodItems = items
  },
  SET_NUTRITIONIST_NOTES(state, notes) {
    state.nutritionistNotes = notes
  },
  SET_CURRENT_RECORD(state, record) {
    state.currentRecord = record
  },
  SET_LOADING(state, loading) {
    state.loading = loading
  },
  ADD_DIETARY_RECORD(state, record) {
    state.dietaryRecords.unshift(record)
  },
  UPDATE_DIETARY_RECORD(state, updatedRecord) {
    const index = state.dietaryRecords.findIndex(record => record.id === updatedRecord.id)
    if (index !== -1) {
      state.dietaryRecords.splice(index, 1, updatedRecord)
    }
  },
  REMOVE_DIETARY_RECORD(state, recordId) {
    state.dietaryRecords = state.dietaryRecords.filter(record => record.id !== recordId)
  },
  ADD_NUTRITIONIST_NOTE(state, note) {
    state.nutritionistNotes.unshift(note)
  }
}

const actions = {
  async fetchDietaryRecords({ commit }, { childId, dateRange }) {
    commit('SET_LOADING', true)
    try {
      const response = await getDietaryRecordsByChild(childId, dateRange)
      commit('SET_DIETARY_RECORDS', response.data || [])
      return response.data
    } catch (error) {
      console.error('获取饮食记录失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async createDietaryRecord({ commit }, recordData) {
    commit('SET_LOADING', true)
    try {
      const response = await createDietaryRecord(recordData)
      commit('ADD_DIETARY_RECORD', response.data)
      return response.data
    } catch (error) {
      console.error('创建饮食记录失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async updateDietaryRecord({ commit }, { recordId, data }) {
    commit('SET_LOADING', true)
    try {
      const response = await updateDietaryRecord(recordId, data)
      commit('UPDATE_DIETARY_RECORD', response.data)
      return response.data
    } catch (error) {
      console.error('更新饮食记录失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async deleteDietaryRecord({ commit }, recordId) {
    commit('SET_LOADING', true)
    try {
      await deleteDietaryRecord(recordId)
      commit('REMOVE_DIETARY_RECORD', recordId)
      return true
    } catch (error) {
      console.error('删除饮食记录失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchFoodItems({ commit }) {
    commit('SET_LOADING', true)
    try {
      const response = await getFoodItems()
      commit('SET_FOOD_ITEMS', response.data || [])
      return response.data
    } catch (error) {
      console.error('获取食物列表失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async createFoodItem({ commit }, foodData) {
    commit('SET_LOADING', true)
    try {
      const response = await createFoodItem(foodData)
      const items = [...state.foodItems, response.data]
      commit('SET_FOOD_ITEMS', items)
      return response.data
    } catch (error) {
      console.error('创建食物项目失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchNutritionistNotes({ commit }, childId) {
    commit('SET_LOADING', true)
    try {
      const response = await getNutritionistNotes(childId)
      commit('SET_NUTRITIONIST_NOTES', response.data || [])
      return response.data
    } catch (error) {
      console.error('获取营养师留言失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async createNutritionistNote({ commit }, noteData) {
    commit('SET_LOADING', true)
    try {
      const response = await createNutritionistNote(noteData)
      commit('ADD_NUTRITIONIST_NOTE', response.data)
      return response.data
    } catch (error) {
      console.error('创建营养师留言失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  }
}

const getters = {
  dietaryRecords: state => state.dietaryRecords,
  foodItems: state => state.foodItems,
  nutritionistNotes: state => state.nutritionistNotes,
  currentRecord: state => state.currentRecord,
  loading: state => state.loading,
  recordsByDate: state => {
    const grouped = {}
    state.dietaryRecords.forEach(record => {
      const date = record.recordDate
      if (!grouped[date]) {
        grouped[date] = []
      }
      grouped[date].push(record)
    })
    return grouped
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

