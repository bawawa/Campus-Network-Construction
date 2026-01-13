# ASD儿童饮食与营养均衡系统 - 前端

基于Vue 2 + Element UI开发的ASD儿童饮食与营养均衡管理系统前端。

## 技术栈

- **框架**: Vue 2.6
- **UI组件库**: Element UI 2.15
- **状态管理**: Vuex 3.6
- **路由**: Vue Router 3.5
- **HTTP请求**: Axios 0.27
- **图表**: ECharts 5.4
- **日期处理**: Moment.js 2.29
- **构建工具**: Webpack 4

## 项目结构

```
frontend/
├── build/                    # Webpack配置文件
├── config/                   # 项目配置文件
├── src/
│   ├── api/                 # API接口封装
│   ├── assets/              # 静态资源
│   ├── components/          # 公共组件
│   ├── router/              # 路由配置
│   ├── store/               # Vuex状态管理
│   ├── utils/               # 工具函数
│   ├── views/               # 页面组件
│   │   ├── children/        # 儿童管理相关页面
│   │   ├── dietary/         # 饮食记录相关页面
│   │   ├── nutritionist/    # 营养师相关页面
│   │   ├── reports/         # 报告相关页面
│   │   ├── recipes/         # 食谱相关页面
│   │   ├── user/            # 用户相关页面
│   │   └── layout/          # 布局组件
│   ├── App.vue              # 根组件
│   └── main.js              # 入口文件
├── static/                  # 静态文件
├── index.html               # HTML模板
├── package.json             # 项目配置
└── README.md                # 项目说明
```

## 主要功能模块

### 1. 用户认证模块
- 用户登录/注册
- 角色权限管理（家长/营养师/管理员）
- 个人信息管理

### 2. 儿童管理模块
- 儿童档案管理
- ASD特质档案
- 饮食限制管理
- 成长记录追踪

### 3. 饮食记录模块
- 多模式饮食记录
- 营养成分分析
- 饮食偏好管理
- 过敏原追踪

### 4. 营养师模块
- 儿童信息管理
- 营养建议提供
- 留言回复功能
- 报告生成

### 5. 数据可视化模块
- 营养摄入图表
- 成长趋势分析
- 饮食结构分析
- 个性化报告

### 6. 食谱推荐模块
- 智能食谱推荐
- 营养均衡计算
- 个性化定制
- 用户反馈收集

## 开发环境搭建

### 环境要求

- Node.js >= 8.9.0
- npm >= 3.0.0

### 安装依赖

```bash
cd frontend
npm install
```

### 启动开发服务器

```bash
npm run dev
```

### 构建生产版本

```bash
npm run build
```

## 配置说明

### API配置

在 `src/utils/axios.js` 中配置后端API地址：

```javascript
const service = axios.create({
  baseURL: 'http://localhost:8081/api', // 后端API地址
  timeout: 10000
})
```

### 环境变量

创建 `.env` 文件配置环境变量：

```
NODE_ENV=development
VUE_APP_API_BASE_URL=http://localhost:8081/api
```

## 开发规范

### 代码规范

- 遵循ESLint代码规范
- 使用Prettier格式化代码
- 组件命名使用PascalCase
- 方法命名使用camelCase

### 目录规范

- `views/`: 页面级组件
- `components/`: 可复用组件
- `api/`: API接口封装
- `utils/`: 工具函数
- `store/`: Vuex状态管理

### 状态管理

使用Vuex进行全局状态管理，按模块划分：

- `user`: 用户状态
- `child`: 儿童信息
- `dietary`: 饮食记录

## 部署说明

### 生产环境部署

1. 构建生产版本：
```bash
npm run build
```

2. 将 `dist/` 目录下的文件部署到Web服务器

3. 配置Nginx反向代理到后端API

### Docker部署

创建 `Dockerfile`：

```dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 常见问题

### 1. 依赖安装失败

```bash
# 清除缓存重新安装
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

### 2. 开发服务器启动失败

检查端口是否被占用：
```bash
lsof -i :8080
```

### 3. 跨域问题

在 `config/index.js` 中配置代理：

```javascript
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

## 版本历史

- v1.0.0: 初始版本，包含基础的用户认证和儿童管理功能
- v1.1.0: 添加饮食记录和营养师模块
- v1.2.0: 集成数据可视化和食谱推荐功能

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码
4. 创建Pull Request

## 许可证

MIT License

