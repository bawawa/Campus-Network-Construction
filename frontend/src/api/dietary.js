import request from '@/utils/axios'

// 饮食记录相关API

export function createDietaryRecord(childId, data) {
  return request({
    url: `/children/${childId}/dietary-records`,
    method: 'post',
    data
  })
}

export function getDietaryRecordsByDate(childId, date) {
  return request({
    url: `/children/${childId}/dietary-records/date/${date}`,
    method: 'get'
  })
}

export function getDietaryRecordsByDateRange(childId, startDate, endDate) {
  return request({
    url: `/children/${childId}/dietary-records/date-range`,
    method: 'get',
    params: { startDate, endDate }
  })
}

export function getDietaryRecordsByMealType(childId, date, mealType) {
  return request({
    url: `/children/${childId}/dietary-records/meal-type/${mealType}`,
    method: 'get',
    params: { date }
  })
}

export function getRecentDietaryRecords(childId, days = 7) {
  return request({
    url: `/children/${childId}/dietary-records/recent`,
    method: 'get',
    params: { days }
  })
}

export function updateDietaryRecord(childId, recordId, data) {
  return request({
    url: `/children/${childId}/dietary-records/${recordId}`,
    method: 'put',
    data
  })
}

export function deleteDietaryRecord(childId, recordId) {
  return request({
    url: `/children/${childId}/dietary-records/${recordId}`,
    method: 'delete'
  })
}

export function getRecordDaysCount(childId, days = 30) {
  return request({
    url: `/children/${childId}/dietary-records/stats/record-days`,
    method: 'get',
    params: { days }
  })
}

// 食物项目相关API - 注意：这些API返回包装对象

export function getFoodsByCategory(category) {
  return request({
    url: `/food-nutrition/category/${category}`,
    method: 'get'
  })
}

export function searchFoodItems(keyword, category) {
  return request({
    url: '/food-nutrition/search',
    method: 'get',
    params: { keyword, category }
  })
}

export function getFoodNutritionDetails(foodId) {
  return request({
    url: `/food-nutrition/${foodId}`,
    method: 'get'
  })
}

export function getHighProteinFoods(limit = 10) {
  return request({
    url: '/food-nutrition/high-protein',
    method: 'get',
    params: { limit }
  })
}

export function getHighFiberFoods(limit = 10) {
  return request({
    url: '/food-nutrition/high-fiber',
    method: 'get',
    params: { limit }
  })
}

export function getHighVitaminCFoods(limit = 10) {
  return request({
    url: '/food-nutrition/high-vitaminc',
    method: 'get',
    params: { limit }
  })
}

export function getHighCalciumFoods(limit = 10) {
  return request({
    url: '/food-nutrition/high-calcium',
    method: 'get',
    params: { limit }
  })
}

export function recommendFoodsByNutrition(nutrient, limit = 10) {
  return request({
    url: '/food-nutrition/recommend',
    method: 'get',
    params: { nutrient, limit }
  })
}

export function initializeFoodDatabase() {
  return request({
    url: '/food-nutrition/initialize',
    method: 'post'
  })
}

export function getNutritionDatabaseStats() {
  return request({
    url: '/food-nutrition/stats',
    method: 'get'
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

export function getNutritionistNotes(childId, page = 0, size = 10) {
  return request({
    url: `/nutritionist-notes/child/${childId}`,
    method: 'get',
    params: { page, size }
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

