package com.own.asd.service;

import com.own.asd.model.system.Role;
import com.own.asd.repository.RoleRepository;
import com.own.asd.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * 创建角色
     */
    public Role createRole(String name, String displayName, String description,
                          Boolean isActive, Integer sortOrder, Set<Long> permissionIds) {

        if (roleRepository.findByName(name).isPresent()) {
            throw new RuntimeException("角色名称已存在");
        }

        Role role = new Role();
        role.setName(name);
        role.setDisplayName(displayName);
        role.setDescription(description);
        role.setIsActive(isActive != null ? isActive : true);
        role.setSortOrder(sortOrder != null ? sortOrder : 0);

        // 设置权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<com.own.asd.model.system.Permission> permissions =
                    permissionRepository.findAllById(permissionIds).stream()
                            .collect(java.util.stream.Collectors.toSet());
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    /**
     * 更新角色
     */
    public Role updateRole(Long roleId, String name, String displayName, String description,
                          Boolean isActive, Integer sortOrder, Set<Long> permissionIds) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));

        if (name != null && !name.equals(role.getName())) {
            if (roleRepository.existsByNameAndIdNot(name, roleId)) {
                throw new RuntimeException("角色名称已存在");
            }
            role.setName(name);
        }

        if (displayName != null) role.setDisplayName(displayName);
        if (description != null) role.setDescription(description);
        if (isActive != null) role.setIsActive(isActive);
        if (sortOrder != null) role.setSortOrder(sortOrder);

        // 更新权限
        if (permissionIds != null) {
            Set<com.own.asd.model.system.Permission> permissions =
                    permissionRepository.findAllById(permissionIds).stream()
                            .collect(java.util.stream.Collectors.toSet());
            role.setPermissions(permissions);
        }

        return roleRepository.save(role);
    }

    /**
     * 删除角色
     */
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    /**
     * 根据ID获取角色
     */
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Long roleId) {
        return roleRepository.findById(roleId);
    }

    /**
     * 根据名称获取角色
     */
    @Transactional(readOnly = true)
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * 获取所有启用的角色
     */
    @Transactional(readOnly = true)
    public List<Role> getAllActiveRoles() {
        return roleRepository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    /**
     * 分页获取启用的角色
     */
    @Transactional(readOnly = true)
    public Page<Role> getActiveRoles(Pageable pageable) {
        return roleRepository.findByIsActiveTrueOrderBySortOrderAsc(pageable);
    }

    /**
     * 根据显示名称搜索角色
     */
    @Transactional(readOnly = true)
    public List<Role> searchRolesByDisplayName(String displayName) {
        return roleRepository.findByDisplayNameContainingIgnoreCaseAndIsActiveTrue(displayName);
    }

    /**
     * 根据权限代码获取拥有该权限的角色
     */
    @Transactional(readOnly = true)
    public List<Role> getRolesByPermission(String permissionCode) {
        return roleRepository.findByPermissionCode(permissionCode);
    }

    /**
     * 检查角色名称是否唯一
     */
    @Transactional(readOnly = true)
    public boolean isRoleNameUnique(String name, Long excludeId) {
        if (excludeId == null) {
            return !roleRepository.findByName(name).isPresent();
        }
        return !roleRepository.existsByNameAndIdNot(name, excludeId);
    }
}

