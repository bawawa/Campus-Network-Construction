import request from '@/utils/axios'

// 饮食记录相关API

export function createDietaryRecord(data) {
  return request({
    url: '/dietary-records',
    method: 'post',
    data
  })
}

export function getDietaryRecordsByChild(childId, dateRange = {}) {
  return request({
    url: `/dietary-records/child/${childId}`,
    method: 'get',
    params: dateRange
  })
}

export function getDietaryRecordById(recordId) {
  return request({
    url: `/dietary-records/${recordId}`,
    method: 'get'
  })
}

export function updateDietaryRecord(recordId, data) {
  return request({
    url: `/dietary-records/${recordId}`,
    method: 'put',
    data
  })
}

export function deleteDietaryRecord(recordId) {
  return request({
    url: `/dietary-records/${recordId}`,
    method: 'delete'
  })
}

export function getDietaryRecordsByDate(childId, date) {
  return request({
    url: `/dietary-records/child/${childId}/date/${date}`,
    method: 'get'
  })
}

// 食物项目相关API

export function createFoodItem(data) {
  return request({
    url: '/food-items',
    method: 'post',
    data
  })
}

export function getFoodItems(category) {
  return request({
    url: '/food-items',
    method: 'get',
    params: { category }
  })
}

export function getFoodItemById(itemId) {
  return request({
    url: `/food-items/${itemId}`,
    method: 'get'
  })
}

export function updateFoodItem(itemId, data) {
  return request({
    url: `/food-items/${itemId}`,
    method: 'put',
    data
  })
}

export function searchFoodItems(keyword) {
  return request({
    url: '/food-items/search',
    method: 'get',
    params: { keyword }
  })
}

// 营养师留言相关API

export function createNutritionistNote(data) {
  return request({
    url: '/nutritionist-notes',
    method: 'post',
    data
  })
}

export function getNutritionistNotes(childId) {
  return request({
    url: `/nutritionist-notes/child/${childId}`,
    method: 'get'
  })
}

export function getNutritionistNoteById(noteId) {
  return request({
    url: `/nutritionist-notes/${noteId}`,
    method: 'get'
  })
}

export function updateNutritionistNote(noteId, data) {
  return request({
    url: `/nutritionist-notes/${noteId}`,
    method: 'put',
    data
  })
}

export function deleteNutritionistNote(noteId) {
  return request({
    url: `/nutritionist-notes/${noteId}`,
    method: 'delete'
  })
}

// 营养分析相关API

export function getNutritionAnalysis(childId, dateRange) {
  return request({
    url: `/nutrition-analysis/child/${childId}`,
    method: 'get',
    params: dateRange
  })
}

export function getDailyNutritionSummary(childId, date) {
  return request({
    url: `/nutrition-analysis/child/${childId}/daily/${date}`,
    method: 'get'
  })
}

export function getWeeklyNutritionSummary(childId, weekStart) {
  return request({
    url: `/nutrition-analysis/child/${childId}/weekly/${weekStart}`,
    method: 'get'
  })
}

