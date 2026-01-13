import request from '@/utils/axios'

// 用户相关API

export function createUser(data) {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

export function getUserById(id) {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

export function getUserByEmail(email) {
  return request({
    url: `/users/email/${email}`,
    method: 'get'
  })
}

export function getUsersByRole(role) {
  return request({
    url: `/users/role/${role}`,
    method: 'get'
  })
}

export function updateUser(id, data) {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

export function getActiveUsers() {
  return request({
    url: '/users/active',
    method: 'get'
  })
}

