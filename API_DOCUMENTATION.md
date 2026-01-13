# API 文档

## 基础信息

- **基础URL**: `http://localhost:8081/api`
- **内容类型**: `application/json`
- **认证**: 部分API需要认证（当前开发阶段大部分API开放）

## 用户管理 API

### 创建用户

**POST** `/users`

创建新的用户账户（家长或营养师）。

**请求体**:
```json
{
  "name": "string",
  "email": "string",
  "password": "string",
  "phone": "string",
  "role": "PARENT|NUTRITIONIST|ADMIN",
  "relationshipType": "FATHER|MOTHER|GUARDIAN|OTHER"
}
```

**响应**:
```json
{
  "id": 1,
  "name": "string",
  "email": "string",
  "phone": "string",
  "role": "PARENT",
  "relationshipType": "MOTHER",
  "isActive": true,
  "createdAt": "2026-01-05T14:34:26.989",
  "updatedAt": "2026-01-05T14:34:26.989"
}
```

### 获取用户信息

**GET** `/users/{id}`

根据ID获取用户信息。

**响应**: 用户对象

### 根据邮箱获取用户

**GET** `/users/email/{email}`

根据邮箱获取用户信息。

**响应**: 用户对象

### 获取用户列表

**GET** `/users/role/{role}`

根据角色获取用户列表。

**参数**:
- `role`: PARENT, NUTRITIONIST, ADMIN

**响应**: 用户对象数组

## 儿童管理 API

### 创建儿童档案

**POST** `/children`

创建新的儿童档案。

**请求体**:
```json
{
  "name": "string",
  "gender": "MALE|FEMALE",
  "birthDate": "YYYY-MM-DD",
  "diagnosisInfo": "string",
  "allergyHistory": "string",
  "preferredFoods": "string",
  "parent": {
    "id": 1
  }
}
```

**响应**: 儿童对象

### 获取儿童列表

**GET** `/children/parent/{parentId}`

获取指定家长的所有儿童。

**响应**: 儿童对象数组

### 获取活跃儿童列表

**GET** `/children/parent/{parentId}/active`

获取指定家长的活跃儿童。

**响应**: 儿童对象数组

### 获取儿童详情

**GET** `/children/{id}/parent/{parentId}`

获取指定儿童的详细信息。

**响应**: 儿童对象

### 获取带ASD档案的儿童信息

**GET** `/children/{id}/with-asd-profile`

获取儿童信息及其ASD特质档案。

**响应**: 儿童对象（包含ASD档案）

## ASD特质档案 API

### 创建ASD特质档案

**POST** `/children/{childId}/asd-profiles`

为指定儿童创建ASD特质档案。

**请求体**:
```json
{
  "allergicFoods": "string",
  "intolerantFoods": "string",
  "colorPreference": "LIKE|DISLIKE|NEUTRAL|SENSITIVE|AVERSIVE",
  "texturePreference": "LIKE|DISLIKE|NEUTRAL|SENSITIVE|AVERSIVE",
  "smellPreference": "LIKE|DISLIKE|NEUTRAL|SENSITIVE|AVERSIVE",
  "eatingBehaviorNotes": "string",
  "specialNotes": "string"
}
```

**响应**: ASD特质档案对象

### 获取ASD特质档案列表

**GET** `/children/{childId}/asd-profiles`

获取指定儿童的所有ASD特质档案。

**响应**: ASD特质档案对象数组

### 获取特定ASD特质档案

**GET** `/children/{childId}/asd-profiles/{profileId}`

获取指定儿童的特定ASD特质档案。

**响应**: ASD特质档案对象

### 更新ASD特质档案

**PUT** `/children/{childId}/asd-profiles/{profileId}`

更新指定儿童的ASD特质档案。

**请求体**: 同上创建接口

**响应**: 更新后的ASD特质档案对象

### 删除ASD特质档案

**DELETE** `/children/{childId}/asd-profiles/{profileId}`

删除指定儿童的ASD特质档案。

**响应**: 204 No Content

## 饮食记录 API

### 创建饮食记录

**POST** `/children/{childId}/dietary-records`

为指定儿童创建饮食记录。

**请求体**:
```json
{
  "recordDate": "YYYY-MM-DD",
  "recordTime": "HH:MM:SS",
  "mealType": "BREAKFAST|LUNCH|DINNER|SNACK|SUPPER",
  "foodItem": {
    "id": 1
  },
  "quantity": 100.00,
  "unit": "克",
  "cookingMethod": "BOILED|STEAMED|FRIED|GRILLED|BAKED|STIR_FRIED|SOUP|OTHER",
  "eatingMood": "WILLING|RELUCTANT|RESISTANT|EXCITED|NEUTRAL|PICKY",
  "behaviorNotes": "string",
  "notes": "string"
}
```

**响应**: 饮食记录对象

### 获取指定日期的饮食记录

**GET** `/children/{childId}/dietary-records/date/{date}`

获取指定儿童在特定日期的饮食记录。

**参数**:
- `date`: YYYY-MM-DD格式

**响应**: 饮食记录对象数组

### 获取日期范围内的饮食记录

**GET** `/children/{childId}/dietary-records/date-range`

获取指定儿童在日期范围内的饮食记录。

**查询参数**:
- `startDate`: 开始日期 (YYYY-MM-DD)
- `endDate`: 结束日期 (YYYY-MM-DD)

**响应**: 饮食记录对象数组

### 获取特定餐次的饮食记录

**GET** `/children/{childId}/dietary-records/meal-type/{mealType}`

获取指定儿童特定日期和餐次的饮食记录。

**参数**:
- `mealType`: BREAKFAST, LUNCH, DINNER, SNACK, SUPPER

**查询参数**:
- `date`: 日期 (YYYY-MM-DD)

**响应**: 饮食记录对象数组

### 获取最近的饮食记录

**GET** `/children/{childId}/dietary-records/recent`

获取指定儿童最近的饮食记录。

**查询参数**:
- `days`: 天数 (默认7天)

**响应**: 饮食记录对象数组

### 更新饮食记录

**PUT** `/children/{childId}/dietary-records/{recordId}`

更新指定儿童的饮食记录。

**请求体**: 同创建接口

**响应**: 更新后的饮食记录对象

### 删除饮食记录

**DELETE** `/children/{childId}/dietary-records/{recordId}`

删除指定儿童的饮食记录。

**响应**: 204 No Content

### 获取记录统计

**GET** `/children/{childId}/dietary-records/stats/record-days`

获取指定儿童的记录天数统计。

**查询参数**:
- `days`: 统计天数 (默认30天)

**响应**:
```json
{
  "count": 25
}
```

## 数据模型说明

### 用户角色 (UserRole)
- `PARENT`: 家长
- `NUTRITIONIST`: 营养师
- `ADMIN`: 管理员

### 关系类型 (RelationshipType)
- `FATHER`: 父亲
- `MOTHER`: 母亲
- `GUARDIAN`: 监护人
- `OTHER`: 其他

### 性别 (Gender)
- `MALE`: 男性
- `FEMALE`: 女性

### 感官偏好 (SensoryPreference)
- `LIKE`: 喜欢
- `DISLIKE`: 不喜欢
- `NEUTRAL`: 中性
- `SENSITIVE`: 敏感
- `AVERSIVE`: 厌恶

### 餐次类型 (MealType)
- `BREAKFAST`: 早餐
- `LUNCH`: 午餐
- `DINNER`: 晚餐
- `SNACK`: 加餐
- `SUPPER`: 夜宵

### 烹饪方式 (CookingMethod)
- `RAW`: 生食
- `BOILED`: 煮
- `STEAMED`: 蒸
- `FRIED`: 炸
- `GRILLED`: 烤
- `BAKED`: 烘
- `STIR_FRIED`: 炒
- `SOUP`: 汤
- `OTHER`: 其他

### 进食情绪 (EatingMood)
- `WILLING`: 愿意
- `RELUCTANT`: 勉强
- `RESISTANT`: 抗拒
- `EXCITED`: 兴奋
- `NEUTRAL`: 中性
- `PICKY`: 挑食

## 错误处理

### 常见HTTP状态码

- `200 OK`: 请求成功
- `201 Created`: 资源创建成功
- `204 No Content`: 删除成功
- `400 Bad Request`: 请求参数错误
- `404 Not Found`: 资源不存在
- `500 Internal Server Error`: 服务器内部错误

### 错误响应格式

```json
{
  "timestamp": "2026-01-05T14:34:26.989",
  "status": 400,
  "error": "Bad Request",
  "message": "错误信息",
  "path": "/api/endpoint"
}
```

## 开发环境

### H2数据库控制台

- **URL**: http://localhost:8081/h2-console
- **JDBC URL**: jdbc:h2:mem:testdb
- **用户名**: sa
- **密码**: (空)

### 日志级别

当前配置为DEBUG级别，可以在application.properties中调整：

```properties
logging.level.com.own.asd=DEBUG
logging.level.org.springframework.security=DEBUG

