package com.own.asd.repository;

import com.own.asd.model.system.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 根据权限代码查找
     */
    Optional<Permission> findByCode(String code);

    /**
     * 查找所有启用的权限
     */
    List<Permission> findByIsActiveTrueOrderBySortOrderAsc();

    /**
     * 分页查找启用的权限
     */
    Page<Permission> findByIsActiveTrueOrderBySortOrderAsc(Pageable pageable);

    /**
     * 根据模块查找权限
     */
    List<Permission> findByModuleAndIsActiveTrueOrderBySortOrderAsc(String module);

    /**
     * 根据父权限ID查找子权限
     */
    List<Permission> findByParentIdAndIsActiveTrueOrderBySortOrderAsc(Long parentId);

    /**
     * 查找根权限（没有父权限的权限）
     */
    List<Permission> findByParentIdIsNullAndIsActiveTrueOrderBySortOrderAsc();

    /**
     * 根据权限类型查找
     */
    List<Permission> findByPermissionTypeAndIsActiveTrueOrderBySortOrderAsc(String permissionType);

    /**
     * 检查权限代码是否已存在
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * 根据模块和权限类型查找
     */
    List<Permission> findByModuleAndPermissionTypeAndIsActiveTrueOrderBySortOrderAsc(
            String module, String permissionType);
}

