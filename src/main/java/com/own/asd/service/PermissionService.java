package com.own.asd.service;

import com.own.asd.model.system.Permission;
import com.own.asd.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * 创建权限
     */
    public Permission createPermission(String code, String name, String description,
                                     String module, String permissionType, Long parentId,
                                     Integer sortOrder, Boolean isActive) {

        if (permissionRepository.findByCode(code).isPresent()) {
            throw new RuntimeException("权限代码已存在");
        }

        Permission permission = new Permission();
        permission.setCode(code);
        permission.setName(name);
        permission.setDescription(description);
        permission.setModule(module);
        permission.setPermissionType(permissionType);
        permission.setParentId(parentId);
        permission.setSortOrder(sortOrder != null ? sortOrder : 0);
        permission.setIsActive(isActive != null ? isActive : true);

        return permissionRepository.save(permission);
    }

    /**
     * 更新权限
     */
    public Permission updatePermission(Long permissionId, String code, String name, String description,
                                     String module, String permissionType, Long parentId,
                                     Integer sortOrder, Boolean isActive) {

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("权限不存在"));

        if (code != null && !code.equals(permission.getCode())) {
            if (permissionRepository.existsByCodeAndIdNot(code, permissionId)) {
                throw new RuntimeException("权限代码已存在");
            }
            permission.setCode(code);
        }

        if (name != null) permission.setName(name);
        if (description != null) permission.setDescription(description);
        if (module != null) permission.setModule(module);
        if (permissionType != null) permission.setPermissionType(permissionType);
        if (parentId != null) permission.setParentId(parentId);
        if (sortOrder != null) permission.setSortOrder(sortOrder);
        if (isActive != null) permission.setIsActive(isActive);

        return permissionRepository.save(permission);
    }

    /**
     * 删除权限
     */
    public void deletePermission(Long permissionId) {
        permissionRepository.deleteById(permissionId);
    }

    /**
     * 根据ID获取权限
     */
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionById(Long permissionId) {
        return permissionRepository.findById(permissionId);
    }

    /**
     * 根据代码获取权限
     */
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionByCode(String code) {
        return permissionRepository.findByCode(code);
    }

    /**
     * 获取所有启用的权限
     */
    @Transactional(readOnly = true)
    public List<Permission> getAllActivePermissions() {
        return permissionRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    /**
     * 分页获取启用的权限
     */
    @Transactional(readOnly = true)
    public Page<Permission> getActivePermissions(Pageable pageable) {
        return permissionRepository.findByIsActiveTrueOrderBySortOrderAsc(pageable);
    }

    /**
     * 根据模块获取权限
     */
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByModule(String module) {
        return permissionRepository.findByModuleAndIsActiveTrueOrderBySortOrderAsc(module);
    }

    /**
     * 获取根权限（没有父权限的权限）
     */
    @Transactional(readOnly = true)
    public List<Permission> getRootPermissions() {
        return permissionRepository.findByParentIdIsNullAndIsActiveTrueOrderBySortOrderAsc();
    }

    /**
     * 根据父权限ID获取子权限
     */
    @Transactional(readOnly = true)
    public List<Permission> getChildPermissions(Long parentId) {
        return permissionRepository.findByParentIdAndIsActiveTrueOrderBySortOrderAsc(parentId);
    }

    /**
     * 根据权限类型获取权限
     */
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByType(String permissionType) {
        return permissionRepository.findByPermissionTypeAndIsActiveTrueOrderBySortOrderAsc(permissionType);
    }

    /**
     * 根据模块和权限类型获取权限
     */
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByModuleAndType(String module, String permissionType) {
        return permissionRepository.findByModuleAndPermissionTypeAndIsActiveTrueOrderBySortOrderAsc(
                module, permissionType);
    }

    /**
     * 检查权限代码是否唯一
     */
    @Transactional(readOnly = true)
    public boolean isPermissionCodeUnique(String code, Long excludeId) {
        if (excludeId == null) {
            return !permissionRepository.findByCode(code).isPresent();
        }
        return !permissionRepository.existsByCodeAndIdNot(code, excludeId);
    }

    /**
     * 构建权限树
     */
    @Transactional(readOnly = true)
    public List<Permission> buildPermissionTree() {
        List<Permission> rootPermissions = getRootPermissions();
        buildPermissionTreeRecursive(rootPermissions);
        return rootPermissions;
    }

    private void buildPermissionTreeRecursive(List<Permission> permissions) {
        for (Permission permission : permissions) {
            List<Permission> children = getChildPermissions(permission.getId());
            if (!children.isEmpty()) {
                // 这里可以设置子权限，如果需要的话
                buildPermissionTreeRecursive(children);
            }
        }
    }
}

