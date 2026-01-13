import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from './utils/axios'
import './assets/styles/global.css'

Vue.use(ElementUI)

Vue.config.productionTip = false

// 全局配置
Vue.prototype.$axios = axios

// 全局过滤器
Vue.filter('formatDate', function(value) {
  if (!value) return ''
  const moment = require('moment')
  return moment(value).format('YYYY-MM-DD')
})

Vue.filter('formatDateTime', function(value) {
  if (!value) return ''
  const moment = require('moment')
  return moment(value).format('YYYY-MM-DD HH:mm:ss')
})

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})

