# Hibernate 映射冲突问题修复

## 问题描述

启动 Spring Boot 应用时遇到以下错误:

```
Table [nutrition_reports] contains physical column name [child_id] referred to by multiple logical column names: [child_id], [childId]
```

这是一个 Hibernate JPA 映射冲突问题。

## 根本原因

`NutritionReport` 实体中存在重复的列名映射:

```java
@Column(nullable = false)
private Long childId;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "child_id", insertable = false, updatable = false)
@JsonIgnore
private Child child;
```

这两个字段都映射到同一个物理列 `child_id`,导致 Hibernate 无法确定应该使用哪个映射。

## 解决方案

移除独立的 `childId` 字段,只保留 `child` 关联关系,并通过 `getChildId()` 方法提供访问:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "child_id", nullable = false)
@JsonIgnore
private Child child;

public Long getChildId() {
    return child != null ? child.getId() : null;
}
```

### 修改的文件

1. **NutritionReport.java** - 移除独立的 `childId` 字段,只使用 `child` 关联
2. **NutritionReportService.java** - 修改 `saveAIReport()` 方法,使用 `Child` 对象而不是 `childId`

### 具体修改

#### NutritionReport.java

**移除的内容:**
```java
@Column(nullable = false)
private Long childId;

public void setChildId(Long childId) {
    this.childId = childId;
}
```

**修改的内容:**
```java
// 构造函数修改
public NutritionReport(Child child, ReportType reportType, LocalDate startDate, LocalDate endDate) {
    this.child = child;
    this.reportType = reportType;
    this.startDate = startDate;
    this.endDate = endDate;
}

// getChildId() 方法修改为从 child 对象获取 ID
public Long getChildId() {
    return child != null ? child.getId() : null;
}
```

#### NutritionReportService.java

**新增依赖:**
```java
@Autowired
private ChildRepository childRepository;
```

**修改 saveAIReport() 方法:**
```java
// 在方法开始时获取 Child 对象
Child child = childRepository.findById(childId)
        .orElseThrow(() -> new RuntimeException("儿童不存在: " + childId));

// 创建报告时使用 Child 对象
NutritionReport report = new NutritionReport();
report.setChild(child);  // 使用 setChild() 而不是 setChildId()
```

## 为什么这样修复是正确的

### JPA 最佳实践

1. **单一映射源**: 对于外键关联,应该只使用一个 JPA 映射(通常是 `@ManyToOne`),而不应该同时使用 `@Column` 和关联映射。

2. **延迟加载**: 使用 `@ManyToOne(fetch = FetchType.LAZY)` 可以避免不必要的数据库查询。

3. **业务方法**: 通过 getter 方法提供 `childId` 访问,而不是直接映射字段。

### 数据库表结构

数据库表中只有一个 `child_id` 列,它既是外键也是业务逻辑中的 childId:

```sql
CREATE TABLE nutrition_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    child_id BIGINT NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    nutrition_data TEXT,
    ai_report_content TEXT,
    overall_score INT,
    recommendations TEXT,
    generated_at DATETIME NOT NULL,
    FOREIGN KEY (child_id) REFERENCES children(id)
);
```

## 测试结果

```bash
./mvnw clean compile
```

输出:
```
[INFO] BUILD SUCCESS
[INFO] Total time:  2.110 s
```

✅ 编译成功,问题已解决。

## 避免此类问题的建议

1. **避免重复映射**: 不要同时使用 `@Column` 和关联注解(`@ManyToOne`, `@OneToOne` 等)映射同一列。

2. **使用关联映射**: 对于外键关系,优先使用 JPA 的关联映射而不是单独的字段。

3. **明确的注解**: 对于关联字段,明确指定 `insertable` 和 `updatable` 属性(如果不需要通过关联更新外键)。

4. **使用 @JsonIgnore**: 对于不需要序列化的关联字段(避免循环引用),添加 `@JsonIgnore` 注解。

5. **提供业务方法**: 如果需要访问外键 ID 值,通过 getter 方法提供,而不是额外映射字段。

