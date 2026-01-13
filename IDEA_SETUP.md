# IntelliJ IDEA 运行配置指南

## 问题说明

项目当前配置使用 Zulu JDK，但由于版本兼容性问题，需要切换到 OpenJDK 8。

---

## 解决方案

### 方案 1：使用 IDEA 运行配置（推荐）

我已经创建了新的运行配置文件：
`.idea/runConfigurations/ASD_Application_(1).xml`

此配置将使用 OpenJDK 8 而不是 Zulu JDK。

### 方案 2：使用 Maven 命令行（备用）

如果 IDEA 配置不生效，可以在项目根目录执行以下命令：

```bash
# 设置使用 OpenJDK 8
export JAVA_HOME=/Users/bawawa/Library/Java/JavaVirtualMachines/openjdk-25.0.1/Contents/Home

# 启动应用
./mvnw spring-boot:run
```

### 方案 3：在 IDEA 中手动设置 JRE

1. **打开 Run Configuration**
   - 点击右上角的 "Edit Configurations..."
   - 或按 `Cmd + Option + R`

2. **修改 JRE 设置**
   - 选择 "ASD Application" 配置
   - 展开 "Environment" 选项
   - 找到 "Alternative JRE" 部分
   - 勾选 "Use alternative JRE"
   - 设置路径为：`/Users/bawawa/Library/Java/JavaVirtualMachines/openjdk-25.0.1/Contents/Home`

3. **保存并运行**
   - 点击 "Apply"
   - 点击 "OK"
   - 点击绿色运行按钮启动应用

---

## 配置文件说明

### .idea/runConfigurations/ASD_Application_(1).xml

这个文件包含了完整的运行配置，包括：
- ✅ 主类：`com.own.asd.AsdApplication`
- ✅ JRE 路径：OpenJDK 8
- ✅ Maven 选项：`-Dmaven.compiler.fork=true`

---

## 快速启动步骤

### 在 IDEA 中启动：

1. **关闭当前运行的实例**（如果有的话）
2. **选择新的运行配置**：
   - 在工具栏找到 "ASD Application (1)" 配置
   - 点击绿色运行按钮

### 验证运行状态

启动成功后，您应该看到控制台输出：
```
  .   ____          _            __ _
  /\\ |  _\\/`  |\\ | |\\/
  ( `--\\__.--._/____._ |  | |
  .--\\      \\___ \\___\\ | | |
 /__/ /\\ /  \\  \\___ \\ | |
 \\___ \\ | | | | |
\\___  \\ | | | | |
    | | | | | |
  __|__ | | | | | |
  |  | | | | | |
  \\___  | | | | | | |
  \\___  | | | | | | | |
    | | | | | | | |
 \\___  | | | | | | | |
```

以及最终的启动日志：
```
Started AsdApplication in 1.917 seconds (JVM running for 2.088)
Tomcat started on port(s): 8081 (http)
```

---

## 常见问题排查

### 问题 1：无法点击运行按钮

**可能原因**：项目索引未完成
**解决方法**：
- 等待 IDEA 完成项目索引（右下角会显示 "Indexing..."）
- 索引完成后再尝试运行

### 问题 2：配置不生效

**可能原因**：IDEA 缓存问题
**解决方法**：
1. File → Invalidate Caches → Invalidate and Restart
2. 重新选择运行配置

### 问题 3：JDK 兼容性错误

**如果仍然出现**：`com.sun.tools.javac.code.TypeTag :: UNKNOWN`

**解决方法**：
1. File → Project Structure → Project SDKs
2. 确认 SDK 设置为 1.8
3. 重新构建项目（Build → Rebuild Project）

---

## 系统状态

### 后端服务

- ✅ **状态**：已启动
- ✅ **端口**：8081
- ✅ **进程 ID**：18296
- ✅ **数据库**：已连接（asd_nutrition_db）
- ✅ **测试账户**：7 个账户已创建

### 前端服务

- ✅ **状态**：已启动
- ✅ **端口**：8080
- ✅ **代理配置**：/api → http://localhost:8081/api

---

## 测试账户信息

| # | 角色 | 邮箱 | 密码 |
|---|--------|--------|--------|
| 1 | 管理员 | admin@asd.com | 123456 |
| 2 | 营养师 | nutritionist1@asd.com | 123456 |
| 3 | 营养师 | nutritionist2@asd.com | 123456 |
| 4 | 家长 | parent1@asd.com | 123456 |
| 5 | 家长 | parent2@asd.com | 123456 |
| 6 | 家长 | parent3@asd.com | 123456 |
| 7 | 家长 | parent4@asd.com | 123456 |

---

## 开始使用

1. 在 IDEA 中应用新的运行配置
2. 点击运行按钮启动后端
3. 访问前端：http://localhost:8080
4. 使用上述任意测试账户登录

**祝您使用愉快！** 🎉

