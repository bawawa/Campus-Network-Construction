import {
  getChildrenByParent,
  createChild,
  getChildById,
  updateChild,
  getASDProfilesByChild,
  createASDProfile,
  updateASDProfile,
  deleteASDProfile
} from '@/api/child'

const state = {
  children: [],
  currentChild: null,
  asdProfiles: [],
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
  async fetchChildren({ commit, rootState }) {
    commit('SET_LOADING', true)
    try {
      const response = await getChildrenByParent(rootState.user.userId)
      commit('SET_CHILDREN', response.data || [])
      return response.data
    } catch (error) {
      console.error('获取儿童列表失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchChildById({ commit, state }, childId) {
    commit('SET_LOADING', true)
    try {
      const response = await getChildById(childId)
      commit('SET_CURRENT_CHILD', response.data)
      return response.data
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
      commit('ADD_CHILD', response.data)
      return response.data
    } catch (error) {
      console.error('创建儿童档案失败:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async updateChild({ commit, rootState }, { childId, data }) {
    commit('SET_LOADING', true)
    try {
      const response = await updateChild(childId, rootState.user.userId, data)
      commit('UPDATE_CHILD', response.data)
      if (state.currentChild && state.currentChild.id === childId) {
        commit('SET_CURRENT_CHILD', response.data)
      }
      return response.data
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
      commit('SET_ASD_PROFILES', response.data || [])
      return response.data
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
      const profiles = [...state.asdProfiles, response.data]
      commit('SET_ASD_PROFILES', profiles)
      return response.data
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
        profile.id === profileId ? response.data : profile
      )
      commit('SET_ASD_PROFILES', profiles)
      return response.data
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
  }
}

const getters = {
  children: state => state.children,
  currentChild: state => state.currentChild,
  asdProfiles: state => state.asdProfiles,
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

