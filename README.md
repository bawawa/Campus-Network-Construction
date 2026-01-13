# ASD儿童饮食与营养均衡系统

## 项目概述

这是一个专为ASD（孤独症谱系障碍）儿童设计的饮食与营养均衡管理系统。系统包含用户管理、儿童档案管理、ASD特质记录、饮食记录、营养师服务等多个模块，旨在帮助家长和营养师更好地管理ASD儿童的饮食健康。

## 技术栈

- **后端框架**: Spring Boot 2.7.18
- **数据库**: H2 (开发环境), MySQL (生产环境)
- **安全框架**: Spring Security
- **数据访问**: Spring Data JPA
- **构建工具**: Maven
- **Java版本**: 8

## 系统架构

### 核心模块

1. **用户模块**
   - 家长用户管理
   - 儿童档案管理
   - 多子女家庭支持

2. **ASD特质档案模块**
   - 过敏食物记录
   - 感官偏好管理
   - 饮食行为记录

3. **智慧饮食记录模块**
   - 多模式饮食记录
   - 结构化数据模型
   - 餐次分类管理

4. **营养师模块** (开发中)
   - 儿童信息查看
   - 专业建议留言
   - 营养报告审核

5. **数据可视化模块** (待开发)
   - 营养摄入趋势图
   - 营养均衡分析
   - 体格指标跟踪

6. **智慧食谱推荐模块** (待开发)
   - 个性化食谱推荐
   - 营养补充建议
   - 闭环优化机制

## 快速开始

### 环境要求

- Java 8
- Maven 3.6+

### 运行步骤

1. 克隆项目
```bash
git clone <repository-url>
cd asd
```

2. 编译项目
```bash
./mvnw clean compile
```

3. 运行应用
```bash
./mvnw spring-boot:run
```

4. 访问应用
- API地址: http://localhost:8081
- H2控制台: http://localhost:8081/h2-console

### API使用示例

#### 创建用户
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "张小明",
    "email": "zhangxiaoming@example.com",
    "password": "password123",
    "role": "PARENT",
    "relationshipType": "MOTHER"
  }'
```

#### 创建儿童档案
```bash
curl -X POST http://localhost:8081/api/children \
  -H "Content-Type: application/json" \
  -d '{
    "name": "小明",
    "gender": "MALE",
    "birthDate": "2018-05-15",
    "diagnosisInfo": "ASD诊断，轻度",
    "allergyHistory": "对花生过敏",
    "preferredFoods": "喜欢米饭，胡萝卜，鸡肉",
    "parent": {
      "id": 1
    }
  }'
```

#### 创建ASD特质档案
```bash
curl -X POST http://localhost:8081/api/children/1/asd-profiles \
  -H "Content-Type: application/json" \
  -d '{
    "allergicFoods": "花生、坚果类",
    "intolerantFoods": "辛辣食物",
    "colorPreference": "LIKE",
    "texturePreference": "NEUTRAL",
    "smellPreference": "SENSITIVE",
    "eatingBehaviorNotes": "喜欢固定的餐具，对食物温度敏感",
    "specialNotes": "用餐时需要安静环境"
  }'
```

## 数据库设计

### 主要实体

1. **User (用户表)**
   - 存储家长和营养师信息
   - 支持多种角色和关系类型

2. **Child (儿童表)**
   - 存储ASD儿童基本信息
   - 关联家长和ASD特质档案

3. **ASDProfile (ASD特质档案表)**
   - 记录过敏食物、感官偏好等
   - 支持多版本档案管理

4. **DietaryRecord (饮食记录表)**
   - 详细的饮食记录信息
   - 支持多种餐次和烹饪方式

5. **FoodItem (食物表)**
   - 食物营养成分数据库
   - 支持预设和自定义食物

## 安全特性

- 基于Spring Security的身份验证
- BCrypt密码加密
- 角色基础的访问控制
- API端点权限管理

## 开发计划

### 已完成功能

- ✅ 项目基础架构搭建
- ✅ 用户模块（家长和儿童管理）
- ✅ ASD特质档案管理
- ✅ 饮食记录模块
- ✅ RESTful API设计
- ✅ 数据库模型设计
- ✅ 安全配置

### 待开发功能

- 🔄 营养师模块
- 📋 数据可视化展示
- 📋 智慧食谱推荐
- 📋 系统管理后台
- 📋 营养成分数据库集成
- 📋 前端界面开发

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请通过以下方式联系我们：
- 邮箱: [your-email@example.com]
- 项目地址: [GitHub repository URL]

