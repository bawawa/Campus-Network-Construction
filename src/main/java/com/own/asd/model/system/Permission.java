package com.own.asd.model.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "permissions")
@Data
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code; // 权限代码，如：user:create, user:update, recipe:view

    @Column(name = "name", length = 100)
    private String name; // 权限名称，如：创建用户、更新用户、查看食谱

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 权限描述

    @Column(name = "module", length = 50)
    private String module; // 所属模块，如：user, recipe, nutrition

    @Column(name = "permission_type")
    private String permissionType; // 权限类型：menu, button, api

    @Column(name = "parent_id")
    private Long parentId; // 父权限ID

    @Column(name = "sort_order")
    private Integer sortOrder = 0; // 排序

    @Column(name = "is_active")
    private Boolean isActive = true; // 是否启用

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

