import request from '@/utils/axios'

/**
 * 食谱推荐相关API
 */

// 获取智能推荐
export function getSmartRecommendations(childId, mealType = null) {
  return request({
    url: `/recipe-recommendations/child/${childId}/smart`,
    method: 'get',
    params: mealType ? { mealType } : {}
  })
}

// 获取个性化推荐
export function getPersonalizedRecommendations(childId) {
  return request({
    url: `/recipe-recommendations/child/${childId}/personalized`,
    method: 'get'
  })
}

// 获取随机推荐
export function getRandomRecommendations(count = 5) {
  return request({
    url: '/recipe-recommendations/random',
    method: 'get',
    params: { count }
  })
}

// 搜索食谱
export function searchRecipes(keyword, mealType = null, maxDifficulty = null, maxCookingTime = null) {
  return request({
    url: '/recipe-recommendations/search',
    method: 'get',
    params: {
      keyword,
      mealType,
      maxDifficulty,
      maxCookingTime
    }
  })
}

// 获取推荐统计信息
export function getRecommendationStats(childId) {
  return request({
    url: `/recipe-recommendations/child/${childId}/stats`,
    method: 'get'
  })
}

// 生成 AI 推荐食谱
export function generateAIRecommendations(childId, mealType = null) {
  return request({
    url: `/recipe-recommendations/child/${childId}/ai-recommend`,
    method: 'post',
    params: mealType ? { mealType } : {}
  })
}

