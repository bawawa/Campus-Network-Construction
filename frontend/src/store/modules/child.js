import {
  addDietaryRestriction,
  createASDProfile,
  createChild,
  deleteASDProfile,
  deleteDietaryRestriction,
  getAllChildren,
  getASDProfilesByChild,
  getChildById,
  getChildrenByParent,
  getDietaryRestrictions,
  updateASDProfile,
  updateChild,
  updateDietaryRestriction
} from '@/api/child'

const state = {
  children: [],
  currentChild: null,
  asdProfiles: [],
  dietaryRestrictions: [],
  loading: false
}

const mutations = {
  SET_CHILDREN(state, children) {
    state.children = children
  },
  SET_CURRENT_CHILD(state, child) {
    state.currentChild = child
  },
  SET_ASD_PROFILES(state, profiles) {
    state.asdProfiles = profiles
  },
  SET_DIETARY_RESTRICTIONS(state, restrictions) {
    state.dietaryRestrictions = restrictions
  },
  SET_LOADING(state, loading) {
    state.loading = loading
  },
  ADD_CHILD(state, child) {
    state.children.push(child)
  },
  UPDATE_CHILD(state, updatedChild) {
    const index = state.children.findIndex(child => child.id === updatedChild.id)
    if (index !== -1) {
      state.children.splice(index, 1, updatedChild)
    }
  },
  REMOVE_CHILD(state, childId) {
    state.children = state.children.filter(child => child.id !== childId)
  }
}

const actions = {
  async fetchChildren({ commit, rootState, rootGetters }) {
    commit('SET_LOADING', true)
    try {
      // 使用 rootGetters 访问 user 模块的 userId getter
      const userId = rootGetters['user/userId']
      if (!userId) {
        throw new Error('用户未登录')
      }
      const response = await getChildrenByParent(userId)
      // axios拦截器已返回 response.data，所以 response 就是数据
      const data = Array.isArray(response) ? response : []
      commit('SET_CHILDREN', data)
      return data
    } catch (error) {
      console.error('获取儿童列表失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchAllChildren({ commit }) {
    commit('SET_LOADING', true)
    try {
      const response = await getAllChildren()
      const data = Array.isArray(response) ? response : []
      commit('SET_CHILDREN', data)
      return data
    } catch (error) {
      console.error('获取所有儿童列表失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchChildById({ commit, state }, childId) {
    commit('SET_LOADING', true)
    try {
      const response = await getChildById(childId)
      commit('SET_CURRENT_CHILD', response)
      return response
    } catch (error) {
      console.error('获取儿童详情失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async createChild({ commit, rootState }, childData) {
    commit('SET_LOADING', true)
    try {
      const dataWithParent = {
        ...childData,
        parentId: rootState.user.userId
      }
      const response = await createChild(dataWithParent)
      commit('ADD_CHILD', response)
      return response
    } catch (error) {
      console.error('创建儿童档案失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async updateChild({ commit, rootGetters, state }, { childId, data }) {
    commit('SET_LOADING', true)
    try {
      const userId = rootGetters['user/userId']
      const response = await updateChild(childId, userId, data)
      commit('UPDATE_CHILD', response)
      if (state.currentChild && state.currentChild.id === childId) {
        commit('SET_CURRENT_CHILD', response)
      }
      return response
    } catch (error) {
      console.error('更新儿童档案失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchASDProfiles({ commit }, childId) {
    commit('SET_LOADING', true)
    try {
      const response = await getASDProfilesByChild(childId)
      const data = Array.isArray(response) ? response : []
      commit('SET_ASD_PROFILES', data)
      return data
    } catch (error) {
      console.error('获取ASD特质档案失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async createASDProfile({ commit }, { childId, data }) {
    commit('SET_LOADING', true)
    try {
      const response = await createASDProfile(childId, data)
      const profiles = [...state.asdProfiles, response]
      commit('SET_ASD_PROFILES', profiles)
      return response
    } catch (error) {
      console.error('创建ASD特质档案失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async updateASDProfile({ commit }, { childId, profileId, data }) {
    commit('SET_LOADING', true)
    try {
      const response = await updateASDProfile(childId, profileId, data)
      const profiles = state.asdProfiles.map(profile =>
        profile.id === profileId ? response : profile
      )
      commit('SET_ASD_PROFILES', profiles)
      return response
    } catch (error) {
      console.error('更新ASD特质档案失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async deleteASDProfile({ commit }, { childId, profileId }) {
    commit('SET_LOADING', true)
    try {
      await deleteASDProfile(childId, profileId)
      const profiles = state.asdProfiles.filter(profile => profile.id !== profileId)
      commit('SET_ASD_PROFILES', profiles)
      return true
    } catch (error) {
      console.error('删除ASD特质档案失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchDietaryRestrictions({ commit }, childId) {
    commit('SET_LOADING', true)
    try {
      const response = await getDietaryRestrictions(childId)
      const data = Array.isArray(response) ? response : []
      commit('SET_DIETARY_RESTRICTIONS', data)
      return data
    } catch (error) {
      console.error('获取饮食限制失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async addDietaryRestriction({ commit }, { childId, data }) {
    commit('SET_LOADING', true)
    try {
      const response = await addDietaryRestriction(childId, data)
      const restrictions = [...state.dietaryRestrictions, response]
      commit('SET_DIETARY_RESTRICTIONS', restrictions)
      return response
    } catch (error) {
      console.error('添加饮食限制失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async updateDietaryRestriction({ commit }, { childId, restrictionId, data }) {
    commit('SET_LOADING', true)
    try {
      const response = await updateDietaryRestriction(childId, restrictionId, data)
      const restrictions = state.dietaryRestrictions.map(r =>
        r.id === restrictionId ? response : r
      )
      commit('SET_DIETARY_RESTRICTIONS', restrictions)
      return response
    } catch (error) {
      console.error('更新饮食限制失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async removeDietaryRestriction({ commit }, { childId, restrictionId }) {
    commit('SET_LOADING', true)
    try {
      await deleteDietaryRestriction(childId, restrictionId)
      const restrictions = state.dietaryRestrictions.filter(r => r.id !== restrictionId)
      commit('SET_DIETARY_RESTRICTIONS', restrictions)
      return true
    } catch (error) {
      console.error('删除饮食限制失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  }
}

const getters = {
  children: state => state.children,
  currentChild: state => state.currentChild,
  asdProfiles: state => state.asdProfiles,
  dietaryRestrictions: state => state.dietaryRestrictions,
  loading: state => state.loading,
  childrenCount: state => state.children.length
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

