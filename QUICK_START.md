# ASD儿童饮食与营养均衡系统 - 快速开始指南

## 🚀 快速开始

### 环境要求

- **后端**: Java 8, Maven 3.6+
- **前端**: Node.js 8.9.0+, npm 3.0+
- **数据库**: H2 (内置) 或 MySQL 5.7+

### 一键启动

#### 1. 启动后端服务

```bash
# 进入项目根目录
cd asd

# 使用Maven启动Spring Boot应用
./mvnw spring-boot:run

# 或者编译后运行
./mvnw clean package
java -jar target/asd-*.jar
```

后端服务将在 http://localhost:8081 启动

#### 2. 启动前端应用

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端应用将在 http://localhost:8080 启动

### 3. 访问系统

- **前端界面**: http://localhost:8080
- **后端API**: http://localhost:8081/api
- **H2控制台**: http://localhost:8081/h2-console

## 📋 系统功能

### 用户角色

1. **家长** - 管理儿童档案，记录饮食信息
2. **营养师** - 提供专业建议，查看儿童信息
3. **管理员** - 系统管理和用户管理

### 主要功能

- ✅ 用户注册和登录
- ✅ 儿童档案管理
- ✅ ASD特质记录
- ✅ 饮食限制管理
- ✅ 仪表盘数据展示
- ✅ 响应式界面设计

## 🔧 配置说明

### 后端配置

修改 `src/main/resources/application.properties`:

```properties
# 服务器端口
server.port=8081

# 数据库配置 (使用H2内置数据库)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA配置
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2控制台
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### 前端配置

修改 `frontend/src/utils/axios.js` 中的API地址:

```javascript
const service = axios.create({
  baseURL: 'http://localhost:8081/api', // 后端API地址
  timeout: 10000
})
```

## 📖 使用指南

### 1. 注册用户

1. 访问 http://localhost:8080
2. 点击"还没有账号？立即注册"
3. 填写用户信息，选择角色（家长/营养师）
4. 完成注册后自动跳转到登录页面

### 2. 登录系统

1. 使用注册的邮箱和密码登录
2. 根据角色显示不同的功能菜单

### 3. 管理儿童档案

**家长用户**:
1. 点击"儿童管理" → "添加儿童"
2. 填写儿童基本信息
3. 添加ASD特质档案
4. 设置饮食限制
5. 完成儿童档案创建

**营养师用户**:
1. 点击"营养师面板"
2. 查看分配的儿童信息
3. 提供营养建议

### 4. 查看仪表盘

- 查看儿童数量统计
- 查看最近活动记录
- 快速访问常用功能

## 🛠️ 开发指南

### 后端开发

```bash
# 编译项目
./mvnw clean compile

# 运行测试
./mvnw test

# 打包应用
./mvnw clean package

# 查看依赖树
./mvnw dependency:tree
```

### 前端开发

```bash
# 启动开发服务器
npm run dev

# 代码规范检查
npm run lint

# 自动修复代码规范
npm run lint:fix

# 构建生产版本
npm run build
```

## 📁 项目结构

### 后端结构

```
src/main/java/com/own/asd/
├── AsdApplication.java          # 应用入口
├── config/                      # 配置类
│   ├── SecurityConfig.java      # 安全配置
│   └── JpaConfig.java          # JPA配置
├── controller/                  # 控制器
│   ├── UserController.java     # 用户管理
│   └── ChildController.java    # 儿童管理
├── model/                       # 实体类
│   ├── user/                   # 用户相关
│   └── nutrition/              # 营养相关
├── repository/                  # 数据访问
│   ├── UserRepository.java     # 用户数据访问
│   └── ChildRepository.java    # 儿童数据访问
└── service/                     # 业务逻辑
    ├── UserService.java        # 用户服务
    └── ChildService.java       # 儿童服务
```

### 前端结构

```
frontend/src/
├── api/                         # API接口
│   ├── user.js                 # 用户API
│   ├── child.js                # 儿童API
│   └── dietary.js              # 饮食API
├── components/                  # 公共组件
│   └── SvgIcon.vue             # SVG图标组件
├── views/                       # 页面组件
│   ├── Login.vue               # 登录页面
│   ├── Dashboard.vue           # 仪表盘
│   ├── children/               # 儿童管理
│   └── layout/                 # 布局组件
├── store/                       # 状态管理
│   ├── index.js                # Vuex入口
│   └── modules/                # 状态模块
├── router/                      # 路由配置
│   └── index.js                # 路由定义
└── utils/                       # 工具函数
    └── axios.js                # HTTP请求
```

## 🔍 调试技巧

### 后端调试

1. **查看日志**: 控制台输出详细的SQL和错误信息
2. **H2控制台**: 访问 http://localhost:8081/h2-console 查看数据库
3. **API测试**: 使用Postman测试REST API

### 前端调试

1. **Vue DevTools**: 安装浏览器扩展调试Vue应用
2. **网络监控**: 使用浏览器开发者工具查看API请求
3. **状态检查**: 使用Vue DevTools查看Vuex状态

## 🚨 常见问题

### 1. 端口冲突

```bash
# 修改后端端口
# 编辑 application.properties
server.port=8082

# 修改前端端口
# 编辑 frontend/config/index.js
devServer: {
  port: 8081
}
```

### 2. 数据库连接问题

```properties
# 使用MySQL替代H2
spring.datasource.url=jdbc:mysql://localhost:3306/asd_db
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 3. 前端构建失败

```bash
# 清除缓存重新安装
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

## 📚 参考资料

- [Spring Boot文档](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/)
- [Vue.js文档](https://v2.vuejs.org/v2/guide/)
- [Element UI文档](https://element.eleme.io/)
- [Maven文档](https://maven.apache.org/guides/)

## 📞 支持

如果遇到问题，请查看:
1. [开发指南](frontend/DEVELOPMENT_GUIDE.md)
2. [前端总结](FRONTEND_SUMMARY.md)
3. [项目总结](PROJECT_SUMMARY.md)

## 🎉 恭喜！

您已经成功启动了ASD儿童饮食与营养均衡系统！现在可以开始体验系统的各项功能了。

**下一步建议**:
1. 注册一个家长账号
2. 添加儿童档案
3. 体验仪表盘功能
4. 查看儿童管理界面

祝您使用愉快！ 🚀

