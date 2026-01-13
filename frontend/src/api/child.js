import request from '@/utils/axios'

// 儿童相关API

export function createChild(data) {
  return request({
    url: '/children',
    method: 'post',
    data
  })
}

export function getChildrenByParent(parentId) {
  return request({
    url: `/children/parent/${parentId}`,
    method: 'get'
  })
}

export function getActiveChildrenByParent(parentId) {
  return request({
    url: `/children/parent/${parentId}/active`,
    method: 'get'
  })
}

export function getChildById(childId) {
  return request({
    url: `/children/${childId}`,
    method: 'get'
  })
}

export function getChildByIdAndParent(id, parentId) {
  return request({
    url: `/children/${id}/parent/${parentId}`,
    method: 'get'
  })
}

export function getChildWithASDProfile(childId) {
  return request({
    url: `/children/${childId}/with-asd-profile`,
    method: 'get'
  })
}

export function updateChild(id, parentId, data) {
  return request({
    url: `/children/${id}/parent/${parentId}`,
    method: 'put',
    data
  })
}

// ASD特质档案相关API

export function createASDProfile(childId, data) {
  return request({
    url: `/children/${childId}/asd-profiles`,
    method: 'post',
    data
  })
}

export function getASDProfilesByChild(childId) {
  return request({
    url: `/children/${childId}/asd-profiles`,
    method: 'get'
  })
}

export function getASDProfile(childId, profileId) {
  return request({
    url: `/children/${childId}/asd-profiles/${profileId}`,
    method: 'get'
  })
}

export function updateASDProfile(childId, profileId, data) {
  return request({
    url: `/children/${childId}/asd-profiles/${profileId}`,
    method: 'put',
    data
  })
}

export function deleteASDProfile(childId, profileId) {
  return request({
    url: `/children/${childId}/asd-profiles/${profileId}`,
    method: 'delete'
  })
}

