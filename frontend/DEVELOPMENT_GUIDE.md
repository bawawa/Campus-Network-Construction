# 前端开发指南

## 环境准备

### 系统要求
- Node.js >= 8.9.0
- npm >= 3.0.0
- 推荐使用 nvm 管理 Node.js 版本

### 安装依赖

```bash
# 进入前端项目目录
cd frontend

# 安装依赖
npm install

# 如果安装失败，尝试清除缓存
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

## 开发流程

### 启动开发服务器

```bash
# 启动开发服务器
npm run dev

# 或者使用
npm start
```

开发服务器将在 http://localhost:8080 启动，支持热重载。

### 代码规范检查

```bash
# 检查代码规范
npm run lint

# 自动修复代码规范问题
npm run lint:fix
```

### 构建生产版本

```bash
# 构建生产版本
npm run build
```

构建完成后，dist目录包含所有生产环境文件。

## 项目结构说明

### 主要目录

- `src/api/`: API接口封装，按模块划分
- `src/components/`: 可复用组件
- `src/views/`: 页面级组件
- `src/store/`: Vuex状态管理
- `src/router/`: 路由配置
- `src/utils/`: 工具函数
- `src/assets/`: 静态资源
- `src/icons/`: SVG图标

### 页面组件结构

```
src/views/
├── Login.vue                    # 登录页面
├── Dashboard.vue               # 仪表盘
├── layout/
│   └── MainLayout.vue          # 主布局
├── children/
│   ├── ChildrenList.vue        # 儿童列表
│   ├── AddChild.vue            # 添加儿童
│   ├── ChildDetail.vue         # 儿童详情
│   └── ASDProfile.vue          # ASD特质档案
├── dietary/
│   └── DietaryRecords.vue      # 饮食记录
├── nutritionist/
│   └── NutritionistPanel.vue   # 营养师面板
├── reports/
│   └── NutritionReports.vue    # 营养报告
├── recipes/
│   └── RecipeRecommendations.vue # 食谱推荐
└── user/
    └── Profile.vue             # 个人信息
```

## 开发规范

### 组件开发规范

1. **组件命名**: 使用PascalCase命名，如`ChildDetail.vue`
2. **组件结构**: 遵循template-script-style结构
3. **Props验证**: 所有props都需要类型验证
4. **事件命名**: 使用camelCase命名事件
5. **样式作用域**: 使用scoped样式避免冲突

### 状态管理规范

1. **模块化**: 按功能模块划分Vuex模块
2. **命名空间**: 使用namespaced避免命名冲突
3. **Action异步**: 异步操作放在actions中
4. **Mutation同步**: mutations必须是同步函数

### API调用规范

1. **统一封装**: 所有API调用通过api目录封装
2. **错误处理**: 统一错误处理和提示
3. **Loading状态**: 重要操作显示loading状态
4. **参数验证**: 调用前验证参数有效性

## 常用开发技巧

### 1. 添加新页面

```javascript
// 1. 在views目录创建页面组件
// src/views/example/ExamplePage.vue

// 2. 在router/index.js添加路由
{
  path: '/example',
  name: 'Example',
  component: () => import('@/views/example/ExamplePage.vue'),
  meta: { requiresAuth: true }
}

// 3. 在MainLayout.vue的菜单中添加链接
<el-menu-item index="/example">
  <i class="el-icon-document"></i>
  <span slot="title">示例页面</span>
</el-menu-item>
```

### 2. 添加新API接口

```javascript
// src/api/newModule.js
import request from '@/utils/axios'

export function getData(params) {
  return request({
    url: '/api/endpoint',
    method: 'get',
    params
  })
}

export function createData(data) {
  return request({
    url: '/api/endpoint',
    method: 'post',
    data
  })
}
```

### 3. 添加Vuex模块

```javascript
// src/store/modules/newModule.js
const state = {
  data: [],
  loading: false
}

const mutations = {
  SET_DATA(state, data) {
    state.data = data
  },
  SET_LOADING(state, loading) {
    state.loading = loading
  }
}

const actions = {
  async fetchData({ commit }) {
    commit('SET_LOADING', true)
    try {
      const response = await getData()
      commit('SET_DATA', response.data)
      return response.data
    } catch (error) {
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  }
}

const getters = {
  data: state => state.data,
  loading: state => state.loading
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

// 在store/index.js中注册
import newModule from './modules/newModule'

export default new Vuex.Store({
  modules: {
    newModule
  }
})
```

### 4. 添加全局组件

```vue
<!-- src/components/GlobalComponent.vue -->
<template>
  <div class="global-component">
    <!-- 组件内容 -->
  </div>
</template>

<script>
export default {
  name: 'GlobalComponent',
  props: {
    // props定义
  }
}
</script>

<style scoped>
.global-component {
  /* 样式定义 */
}
</style>

<!-- 在main.js中全局注册 -->
import GlobalComponent from '@/components/GlobalComponent'
Vue.component('global-component', GlobalComponent)
```

## 调试技巧

### 1. Vue DevTools

安装Vue DevTools浏览器扩展，可以：
- 查看组件层级结构
- 检查Vuex状态
- 调试路由信息
- 性能分析

### 2. 网络请求调试

- 使用浏览器开发者工具Network面板
- 检查API请求和响应
- 查看请求参数和返回数据

### 3. 状态调试

```javascript
// 在组件中打印Vuex状态
mounted() {
  console.log('User info:', this.$store.state.user.userInfo)
  console.log('Children:', this.$store.state.child.children)
}

// 使用Vue DevTools查看状态变化
```

### 4. 错误处理

```javascript
// 全局错误处理
Vue.config.errorHandler = (err, vm, info) => {
  console.error('Vue Error:', err)
  console.error('Component:', vm)
  console.error('Info:', info)
}

// API错误处理
async fetchData() {
  try {
    const response = await api.getData()
    // 处理数据
  } catch (error) {
    console.error('API Error:', error)
    this.$message.error('获取数据失败')
  }
}
```

## 性能优化

### 1. 组件懒加载

```javascript
// 路由懒加载
const ExamplePage = () => import('@/views/example/ExamplePage.vue')

// 组件懒加载
components: {
  LazyComponent: () => import('@/components/LazyComponent.vue')
}
```

### 2. 列表优化

```vue
<!-- 使用v-for时添加key -->
<el-table :data="items" :key="item.id">
  <!-- 表格内容 -->
</el-table>

<!-- 大数据量使用虚拟滚动 -->
<el-table
  :data="items"
  height="400"
  :row-height="50"
>
  <!-- 表格内容 -->
</el-table>
```

### 3. 图片优化

```vue
<!-- 使用懒加载 -->
<img v-lazy="imageUrl" alt="图片">

<!-- 响应式图片 -->
<picture>
  <source media="(max-width: 768px)" :srcset="smallImage">
  <source media="(min-width: 769px)" :srcset="largeImage">
  <img :src="defaultImage" alt="图片">
</picture>
```

## 常见问题解决

### 1. 依赖安装失败

```bash
# 清除缓存重新安装
rm -rf node_modules package-lock.json
npm cache clean --force
npm install

# 使用淘宝镜像
npm install --registry=https://registry.npm.taobao.org
```

### 2. 开发服务器启动失败

```bash
# 检查端口占用
lsof -i :8080

# 更换端口
# 修改config/index.js中的port配置
```

### 3. 跨域问题

```javascript
// config/index.js中配置代理
dev: {
  proxyTable: {
    '/api': {
      target: 'http://localhost:8081',
      changeOrigin: true,
      pathRewrite: {
        '^/api': '/api'
      }
    }
  }
}
```

### 4. 样式冲突

```vue
<!-- 使用scoped样式 -->
<style scoped>
.component {
  /* 样式只作用于当前组件 */
}
</style>

<!-- 使用CSS Modules -->
<style module>
.component {
  /* 样式会被编译为唯一类名 */
}
</style>
```

## 部署说明

### 1. 生产环境构建

```bash
npm run build
```

### 2. Nginx配置

```nginx
server {
  listen 80;
  server_name your-domain.com;

  root /path/to/dist;
  index index.html;

  location / {
    try_files $uri $uri/ /index.html;
  }

  # API代理
  location /api/ {
    proxy_pass http://localhost:8081/api/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }
}
```

### 3. Docker部署

```dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 版本控制

### Git提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

### 分支管理

- `master`: 主分支，生产环境代码
- `develop`: 开发分支，集成最新功能
- `feature/*`: 功能分支，开发新功能
- `hotfix/*`: 紧急修复分支

## 参考资料

- [Vue.js官方文档](https://vuejs.org/)
- [Element UI文档](https://element.eleme.io/)
- [Vuex文档](https://vuex.vuejs.org/)
- [Vue Router文档](https://router.vuejs.org/)
- [Webpack文档](https://webpack.js.org/)
- [ESLint文档](https://eslint.org/)

