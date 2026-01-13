package com.own.asd.model.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dictionaries")
@Data
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Dictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String type; // 字典类型，如：meal_type, age_group, food_category

    @Column(nullable = false, length = 100)
    private String code; // 字典代码

    @Column(name = "display_name", length = 200)
    private String displayName; // 显示名称

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 描述

    @Column(name = "sort_order")
    private Integer sortOrder = 0; // 排序

    @Column(name = "is_active")
    private Boolean isActive = true; // 是否启用

    @Column(name = "parent_id")
    private Long parentId; // 父级ID，用于层级结构

    @Column(name = "extra_data", columnDefinition = "TEXT")
    private String extraData; // 额外数据，JSON格式

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

