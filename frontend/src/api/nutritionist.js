import request from '@/utils/axios'

/**
 * 营养师留言相关API
 */

// 创建营养师留言
export function createNutritionistNote(data) {
  return request({
    url: '/nutritionist-notes',
    method: 'post',
    data: null,
    params: data
  })
}

// 获取儿童的所有留言
export function getNotesByChildId(childId, page = 0, size = 10) {
  return request({
    url: `/nutritionist-notes/child/${childId}`,
    method: 'get',
    params: { page, size }
  })
}

// 获取营养师的留言
export function getNotesByNutritionistId(nutritionistId, page = 0, size = 10) {
  return request({
    url: `/nutritionist-notes/nutritionist/${nutritionistId}`,
    method: 'get',
    params: { page, size }
  })
}

// 获取儿童的未读留言
export function getUnreadNotes(childId) {
  return request({
    url: `/nutritionist-notes/child/${childId}/unread`,
    method: 'get'
  })
}

// 根据留言类型获取留言
export function getNotesByType(childId, noteType) {
  return request({
    url: `/nutritionist-notes/child/${childId}/type/${noteType}`,
    method: 'get'
  })
}

// 标记留言为已读
export function markNoteAsRead(noteId) {
  return request({
    url: `/nutritionist-notes/${noteId}/mark-read`,
    method: 'put'
  })
}

// 家长回复留言
export function addParentResponse(noteId, response) {
  return request({
    url: `/nutritionist-notes/${noteId}/response`,
    method: 'put',
    params: { response }
  })
}

// 更新留言
export function updateNote(noteId, data) {
  return request({
    url: `/nutritionist-notes/${noteId}`,
    method: 'put',
    data: null,
    params: data
  })
}

// 删除留言
export function deleteNote(noteId) {
  return request({
    url: `/nutritionist-notes/${noteId}`,
    method: 'delete'
  })
}

// 获取留言统计信息
export function getNoteStats(childId) {
  return request({
    url: `/nutritionist-notes/child/${childId}/stats`,
    method: 'get'
  })
}

// 获取留言详情
export function getNoteById(noteId) {
  return request({
    url: `/nutritionist-notes/${noteId}`,
    method: 'get'
  })
}

