import { getUserByEmail, createUser } from '@/api/user'

const state = {
  userInfo: JSON.parse(localStorage.getItem('userInfo')) || null,
  token: localStorage.getItem('token') || '',
  isAuthenticated: !!localStorage.getItem('token')
}

const mutations = {
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  },
  SET_TOKEN(state, token) {
    state.token = token
    state.isAuthenticated = !!token
    localStorage.setItem('token', token)
  },
  LOGOUT(state) {
    state.userInfo = null
    state.token = ''
    state.isAuthenticated = false
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
  }
}

const actions = {
  async login({ commit }, { email, password }) {
    try {
      // 这里应该调用后端登录API
      // 暂时模拟登录过程
      const response = await getUserByEmail(email)
      if (response.data) {
        commit('SET_USER_INFO', response.data)
        commit('SET_TOKEN', 'mock-token-' + Date.now())
        return response.data
      }
      throw new Error('登录失败')
    } catch (error) {
      throw error
    }
  },

  async register({ commit }, userData) {
    try {
      const response = await createUser(userData)
      return response.data
    } catch (error) {
      throw error
    }
  },

  logout({ commit }) {
    commit('LOGOUT')
  },

  async fetchUserInfo({ commit, state }) {
    if (state.token && state.userInfo?.email) {
      try {
        const response = await getUserByEmail(state.userInfo.email)
        commit('SET_USER_INFO', response.data)
        return response.data
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    }
  }
}

const getters = {
  userInfo: state => state.userInfo,
  isAuthenticated: state => state.isAuthenticated,
  userRole: state => state.userInfo?.role,
  userId: state => state.userInfo?.id
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

