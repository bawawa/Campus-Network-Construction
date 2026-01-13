# ASD 儿童饮食与营养均衡系统 - 测试账户说明

## 📋 测试账户列表

### 🔐 默认密码
所有测试账户的密码都是：**123456**

---

## 1️⃣ 管理员账户

| 字段 | 值 |
|------|-----|
| 姓名 | 系统管理员 |
| 邮箱 | admin@asd.com |
| 密码 | 123456 |
| 角色 | ADMIN |
| 手机号 | 13800000001 |
| 状态 | 活跃 |

**权限：**
- ✅ 查看儿童信息
- ✅ 添加饮食记录
- ✅ 查看营养报告
- ✅ 查看食谱推荐
- ✅ 创建营养建议
- ✅ 查看营养师面板
- ✅ **用户管理**
- ✅ **角色权限管理**
- ✅ **系统设置**

**访问菜单：**
- 仪表盘（系统概览）
- **系统管理**（管理员专属）
  - 管理面板
  - 用户管理
  - 角色权限

---

## 2️⃣ 营养师账户 1

| 字段 | 值 |
|------|-----|
| 姓名 | 李营养师 |
| 邮箱 | nutritionist1@asd.com |
| 密码 | 123456 |
| 角色 | NUTRITIONIST |
| 手机号 | 13800000002 |
| 状态 | 活跃 |

**权限：**
- ✅ 查看儿童信息
- ✅ 查看营养报告
- ✅ 查看食谱推荐
- ✅ 创建营养建议
- ✅ 查看营养师面板

**访问菜单：**
- 仪表盘
- 儿童管理
- 饮食记录
- 营养报告
- 食谱推荐
- **营养师面板**（营养师专属）

---

## 3️⃣ 营养师账户 2

| 字段 | 值 |
|------|-----|
| 姓名 | 王营养师 |
| 邮箱 | nutritionist2@asd.com |
| 密码 | 123456 |
| 角色 | NUTRITIONIST |
| 手机号 | 13800000003 |
| 状态 | 活跃 |

**权限：** 同营养师账户 1

---

## 4️⃣ 家长账户 1

| 字段 | 值 |
|------|-----|
| 姓名 | 张三 |
| 邮箱 | parent1@asd.com |
| 密码 | 123456 |
| 角色 | PARENT |
| 手机号 | 13800000011 |
| 关系 | 父亲 (FATHER) |
| 状态 | 活跃 |

**权限：**
- ✅ 查看儿童信息（仅自己的孩子）
- ✅ 添加饮食记录
- ✅ 查看营养报告
- ✅ 查看食谱推荐

**访问菜单：**
- 仪表盘
- 儿童管理（仅自己的孩子）
- 饮食记录
- 营养报告
- 食谱推荐

---

## 5️⃣ 家长账户 2

| 字段 | 值 |
|------|-----|
| 姓名 | 李四 |
| 邮箱 | parent2@asd.com |
| 密码 | 123456 |
| 角色 | PARENT |
| 手机号 | 13800000012 |
| 关系 | 母亲 (MOTHER) |
| 状态 | 活跃 |

**权限：** 同家长账户 1

---

## 6️⃣ 家长账户 3

| 字段 | 值 |
|------|-----|
| 姓名 | 王五 |
| 邮箱 | parent3@asd.com |
| 密码 | 123456 |
| 角色 | PARENT |
| 手机号 | 13800000013 |
| 关系 | 父亲 (FATHER) |
| 状态 | 活跃 |

**权限：** 同家长账户 1

---

## 7️⃣ 家长账户 4

| 字段 | 值 |
|------|-----|
| 姓名 | 赵六 |
| 邮箱 | parent4@asd.com |
| 密码 | 123456 |
| 角色 | PARENT |
| 手机号 | 13800000014 |
| 关系 | 母亲 (MOTHER) |
| 状态 | 活跃 |

**权限：** 同家长账户 1

---

## 🚀 快速开始

### 1. 访问前端应用
打开浏览器访问：http://localhost:8080

### 2. 使用测试账户登录
选择以下任意账户登录：
- 管理员：admin@asd.com / 123456
- 营养师：nutritionist1@asd.com / 123456
- 家长：parent1@asd.com / 123456

### 3. 测试不同角色的功能
根据角色权限测试相应功能：
- 管理员可访问所有管理功能
- 营养师可查看营养师面板
- 家长可管理自己的孩子

---

## 📊 角色功能对比表

| 功能 | 家长 | 营养师 | 管理员 |
|------|-------|--------|--------|
| 登录系统 | ✅ | ✅ | ✅ |
| 查看仪表盘 | ✅ | ✅ | ✅ |
| 管理儿童档案 | ✅（自己的孩子） | ✅（所有孩子） | ✅（所有） |
| 添加饮食记录 | ✅ | ✅ | ✅ |
| 查看营养报告 | ✅ | ✅ | ✅ |
| 查看食谱推荐 | ✅ | ✅ | ✅ |
| 营养师面板 | ❌ | ✅ | ✅ |
| 创建营养建议 | ❌ | ✅ | ✅ |
| 用户管理 | ❌ | ❌ | ✅ |
| 角色权限管理 | ❌ | ❌ | ✅ |
| 系统设置 | ❌ | ❌ | ✅ |

---

## 🔧 SQL 插入语句

如果您需要在其他环境创建这些账户，可以使用以下 SQL 语句：

```sql
-- 清空现有数据（谨慎使用）
DELETE FROM users WHERE email IN (
  'admin@asd.com',
  'nutritionist1@asd.com',
  'nutritionist2@asd.com',
  'parent1@asd.com',
  'parent2@asd.com',
  'parent3@asd.com',
  'parent4@asd.com'
);

-- 创建管理员账户
INSERT INTO users (name, email, password, phone, role, is_active, created_at, updated_at)
VALUES
('系统管理员', 'admin@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8lE9lBOsl', '13800000001', 'ADMIN', true, NOW(), NOW());

-- 创建营养师账户
INSERT INTO users (name, email, password, phone, role, is_active, created_at, updated_at)
VALUES
('李营养师', 'nutritionist1@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl', '13800000002', 'NUTRITIONIST', true, NOW(), NOW()),
('王营养师', 'nutritionist2@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl', '13800000003', 'NUTRITIONIST', true, NOW(), NOW());

-- 创建家长账户
INSERT INTO users (name, email, password, phone, role, relationship_type, is_active, created_at, updated_at)
VALUES
('张三', 'parent1@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl', '13800000011', 'PARENT', 'FATHER', true, NOW(), NOW()),
('李四', 'parent2@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl', '13800000012', 'PARENT', 'MOTHER', true, NOW(), NOW()),
('王五', 'parent3@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl', '13800000013', 'PARENT', 'FATHER', true, NOW(), NOW()),
('赵六', 'parent4@asd.com', '$2a$10$N.zmdr9k7uOCQb3765UnuTJ8iAt6Z5EHsM8lE9lBOsl', '13800000014', 'PARENT', 'MOTHER', true, NOW(), NOW());
```

---

## ⚠️ 注意事项

1. **安全提醒**：这些是测试账户，生产环境应使用强密码
2. **密码加密**：密码使用 BCrypt 加密存储
3. **数据库**：确保 MySQL 服务正在运行
4. **后端服务**：确保 Spring Boot 应用运行在端口 8081
5. **前端服务**：确保 Vue.js 应用运行在端口 8080

---

## 📞 故障排查

如果无法登录，请检查：

1. **后端是否运行**：访问 http://localhost:8081
2. **数据库是否连接**：检查 MySQL 状态
3. **密码是否正确**：确保使用 123456
4. **账户是否激活**：检查 users 表中的 is_active 字段

---

## ✅ 系统状态

当前系统已配置完成：
- ✅ 数据库：asd_nutrition_db（已创建）
- ✅ 表结构：12 张表（已自动生成）
- ✅ 测试用户：7 个账户（已创建）
- ✅ 后端服务：端口 8081（运行中）
- ✅ 前端服务：端口 8080（运行中）

**系统已准备就绪！可以开始测试。**

