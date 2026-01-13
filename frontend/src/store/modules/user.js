import {changePassword, createUser, getUserByEmail, login, updateUser} from '@/api/user'

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
      const response = await login({ email, password })
      commit('SET_USER_INFO', response.user)
      commit('SET_TOKEN', response.token)
      return response.user
    } catch (error) {
      throw new Error(error.response?.data || '登录失败，请检查邮箱和密码')
    }
  },

  async register({ commit }, userData) {
    try {
      const response = await createUser(userData)
      return response
    } catch (error) {
      throw error
    }
  },

  logout({ commit }) {
    commit('LOGOUT')
  },

  async fetchUserInfo({ commit, state }) {
    if (state.token && state.userInfo && state.userInfo.email) {
      try {
        const response = await getUserByEmail(state.userInfo.email)
        commit('SET_USER_INFO', response)
        return response
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    }
  },

  async updateUserInfo({ commit, state }, userData) {
    if (state.userInfo && state.userInfo.id) {
      try {
        const response = await updateUser(state.userInfo.id, userData)
        commit('SET_USER_INFO', response)
        return response
      } catch (error) {
        throw new Error(error.response?.data || '更新用户信息失败')
      }
    }
  },

  async changePassword({ state, dispatch }, passwordData) {
    if (state.userInfo && state.userInfo.id) {
      try {
        await changePassword(state.userInfo.id, passwordData)
        this.$message && this.$message.success('密码修改成功')
      } catch (error) {
        throw new Error(error.response?.data || '修改密码失败')
      }
    }
  }
}

const getters = {
  userInfo: state => state.userInfo,
  isAuthenticated: state => state.isAuthenticated,
  userRole: state => (state.userInfo && state.userInfo.role),
  userId: state => (state.userInfo && state.userInfo.id)
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

