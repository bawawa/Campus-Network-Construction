# JPQL 查询验证失败修复

## 问题描述

启动 Spring Boot 应用时遇到以下错误:

```
org.springframework.data.repository.query.QueryCreationException: Could not create query for public abstract java.util.Optional com.own.asd.repository.NutritionReportRepository.findLatestAIReport(java.lang.Long); Reason: Validation failed for query for method public abstract java.util.Optional com.own.asd.repository.NutritionReportRepository.findLatestAIReport(java.lang.Long)!; nested exception is java.lang.IllegalArgumentException: Validation failed for query for method public abstract java.util.Optional com.own.asd.repository.NutritionReportRepository.findLatestAIReport(java.lang.Long)!
```

## 根本原因

在之前的修复中，我们从 `NutritionReport` 实体中移除了 `childId` 字段，改用 `child` 关联。但是，`NutritionReportRepository` 中的 JPQL 查询仍然引用了不存在的 `childId` 字段：

```java
@Query("SELECT r FROM NutritionReport r WHERE r.childId = :childId ...")
```

由于 `NutritionReport` 实体中不再有 `childId` 属性，导致 JPQL 验证失败。

## 解决方案

修改 JPQL 查询，将 `r.childId` 替换为 `r.child.id`，通过关联对象访问 ID。

### 修改的文件

**NutritionReportRepository.java**

**修改前:**
```java
@Query("SELECT r FROM NutritionReport r WHERE r.childId = :childId AND r.reportType = 'AI' ORDER BY r.generatedAt DESC")
Optional<NutritionReport> findLatestAIReport(@Param("childId") Long childId);

@Query("SELECT r FROM NutritionReport r WHERE r.childId = :childId ...")
List<NutritionReport> findOverlappingReports(...);
```

**修改后:**
```java
@Query("SELECT r FROM NutritionReport r WHERE r.child.id = :childId AND r.reportType = 'AI' ORDER BY r.generatedAt DESC")
Optional<NutritionReport> findLatestAIReport(@Param("childId") Long childId);

@Query("SELECT r FROM NutritionReport r WHERE r.child.id = :childId ...")
List<NutritionReport> findOverlappingReports(...);
```

## 测试结果

```bash
./mvnw clean compile
```

输出:
```
[INFO] BUILD SUCCESS
[INFO] Total time:  2.392 s
```

✅ 编译成功，问题已解决。

## 总结

当修改实体类的字段或关联关系时，必须同步检查并更新所有引用该字段的 JPQL 查询、Criteria 查询以及方法名查询。对于从简单字段(`childId`)迁移到关联对象(`child`)的情况，JPQL 中通常需要改为路径表达式(`child.id`)。

