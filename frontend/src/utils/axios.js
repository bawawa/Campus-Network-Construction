import axios from 'axios'
import {Message, MessageBox} from 'element-ui'
import store from '@/store'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8081/api', // 后端API地址
  timeout: 120000, // 请求超时时间 120秒（AI生成报告需要较长时间）
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = store.state.user.token
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    const res = response.data

    // 如果响应状态码不是200-300，视为错误
    if (response.status >= 200 && response.status < 300) {
      return res
    }

    const errorMsg = res.message || '请求失败'
    Message.error(errorMsg)
    return Promise.reject(new Error(errorMsg))
  },
  error => {
    // 对响应错误做点什么
    console.error('响应错误:', error)

    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，清除用户信息并跳转到登录页
          MessageBox.confirm('登录状态已过期，请重新登录', '提示', {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            store.dispatch('user/logout')
            window.location.reload()
          })
          break
        case 403:
          Message.error('没有权限访问该资源')
          break
        case 404:
          Message.error('请求的资源不存在')
          break
        case 500:
          Message.error('服务器内部错误')
          break
        default:
          Message.error((error.response.data && error.response.data.message) || '请求失败')
      }
    } else if (error.message.includes('timeout')) {
      Message.error('请求超时，请检查网络连接')
    } else {
      Message.error('网络错误，请检查网络连接')
    }

    return Promise.reject(error)
  }
)

export default service

