import {
  addParentResponse,
  createNutritionistNote,
  deleteNote,
  getNoteById,
  getNotesByChildId,
  getNotesByNutritionistId,
  getNotesByType,
  getNoteStats,
  getNutritionistStats,
  getUnreadNotes,
  markNoteAsRead,
  updateNote
} from '@/api/nutritionist'

const state = {
  notes: [],
  currentNote: null,
  noteStats: null,
  unreadNotes: [],
  pagination: {
    totalElements: 0,
    totalPages: 0,
    currentPage: 0,
    pageSize: 10
  },
  loading: false
}

const mutations = {
  SET_NOTES(state, notes) {
    state.notes = notes
  },
  SET_CURRENT_NOTE(state, note) {
    state.currentNote = note
  },
  SET_NOTE_STATS(state, stats) {
    state.noteStats = stats
  },
  SET_UNREAD_NOTES(state, notes) {
    state.unreadNotes = notes
  },
  SET_PAGINATION(state, pagination) {
    state.pagination = pagination
  },
  SET_LOADING(state, loading) {
    state.loading = loading
  },
  ADD_NOTE(state, note) {
    state.notes.unshift(note)
  },
  UPDATE_NOTE(state, updatedNote) {
    const index = state.notes.findIndex(note => note.id === updatedNote.id)
    if (index !== -1) {
      state.notes.splice(index, 1, updatedNote)
    }
  },
  DELETE_NOTE(state, noteId) {
    state.notes = state.notes.filter(note => note.id !== noteId)
  },
  MARK_NOTE_AS_READ(state, noteId) {
    const note = state.notes.find(note => note.id === noteId)
    if (note) {
      note.isRead = true
    }
  }
}

const actions = {
  // 创建营养师留言
  async createNote({ commit }, data) {
    try {
      commit('SET_LOADING', true)
      const response = await createNutritionistNote(data)
      if (response.success) {
        commit('ADD_NOTE', response.data)
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 获取儿童的所有留言
  async getNotesByChildId({ commit }, { childId, page = 0, size = 10 }) {
    try {
      commit('SET_LOADING', true)
      const response = await getNotesByChildId(childId, page, size)
      if (response.success) {
        commit('SET_NOTES', response.data)
        commit('SET_PAGINATION', {
          totalElements: response.totalElements,
          totalPages: response.totalPages,
          currentPage: response.currentPage,
          pageSize: response.pageSize
        })
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 获取营养师的留言
  async getNotesByNutritionistId({ commit }, { nutritionistId, page = 0, size = 10 }) {
    try {
      commit('SET_LOADING', true)
      const response = await getNotesByNutritionistId(nutritionistId, page, size)
      if (response.success) {
        commit('SET_NOTES', response.data)
        commit('SET_PAGINATION', {
          totalElements: response.totalElements,
          totalPages: response.totalPages,
          currentPage: response.currentPage,
          pageSize: response.pageSize
        })
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 获取未读留言
  async getUnreadNotes({ commit }, childId) {
    try {
      const response = await getUnreadNotes(childId)
      if (response.success) {
        commit('SET_UNREAD_NOTES', response.data)
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    }
  },

  // 根据类型获取留言
  async getNotesByType({ commit }, { childId, noteType }) {
    try {
      const response = await getNotesByType(childId, noteType)
      if (response.success) {
        commit('SET_NOTES', response.data)
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    }
  },

  // 标记留言为已读
  async markNoteAsRead({ commit }, noteId) {
    try {
      const response = await markNoteAsRead(noteId)
      if (response.success) {
        commit('MARK_NOTE_AS_READ', noteId)
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    }
  },

  // 添加家长回复
  async addParentResponse({ commit }, { noteId, response }) {
    try {
      const res = await addParentResponse(noteId, response)
      if (res.success) {
        commit('UPDATE_NOTE', res.data)
        return res
      } else {
        throw new Error(res.message)
      }
    } catch (error) {
      throw error
    }
  },

  // 更新留言
  async updateNote({ commit }, { noteId, data }) {
    try {
      commit('SET_LOADING', true)
      const response = await updateNote(noteId, data)
      if (response.success) {
        commit('UPDATE_NOTE', response.data)
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  // 删除留言
  async deleteNote({ commit }, noteId) {
    try {
      const response = await deleteNote(noteId)
      if (response.success) {
        commit('DELETE_NOTE', noteId)
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    }
  },

  // 获取留言统计
  async getNoteStats({ commit }, childId) {
    try {
      const response = await getNoteStats(childId)
      if (response.success) {
        commit('SET_NOTE_STATS', response.data)
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    }
  },

  // 获取留言详情
  async getNoteById({ commit }, noteId) {
    try {
      const response = await getNoteById(noteId)
      if (response.success) {
        commit('SET_CURRENT_NOTE', response.data)
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    }
  },

  // 获取营养师统计数据
  async getNutritionistStats({ commit }, nutritionistId) {
    try {
      const response = await getNutritionistStats(nutritionistId)
      if (response.success) {
        commit('SET_NOTE_STATS', response.data)
        return response
      } else {
        throw new Error(response.message)
      }
    } catch (error) {
      throw error
    }
  }
}

const getters = {
  notes: state => state.notes,
  currentNote: state => state.currentNote,
  noteStats: state => state.noteStats,
  unreadNotes: state => state.unreadNotes,
  pagination: state => state.pagination,
  loading: state => state.loading,
  unreadCount: state => state.unreadNotes.length,
  highPriorityNotes: state => state.notes.filter(note => note.priority >= 4)
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

