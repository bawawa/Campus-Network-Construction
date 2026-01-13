package com.own.asd.controller;

import com.own.asd.model.system.Role;
import com.own.asd.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/system/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 创建角色
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(
            @RequestParam @Valid String name,
            @RequestParam @Valid String displayName,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "true") Boolean isActive,
            @RequestParam(defaultValue = "0") Integer sortOrder,
            @RequestParam(required = false) Set<Long> permissionIds) {

        try {
            Role role = roleService.createRole(name, displayName, description, isActive, sortOrder, permissionIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "角色创建成功");
            response.put("data", role);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "角色创建失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新角色
     */
    @PutMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable Long roleId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Integer sortOrder,
            @RequestParam(required = false) Set<Long> permissionIds) {

        try {
            Role role = roleService.updateRole(roleId, name, displayName, description, isActive, sortOrder, permissionIds);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "角色更新成功");
            response.put("data", role);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "角色更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable Long roleId) {
        try {
            roleService.deleteRole(roleId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "角色删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "角色删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取角色列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Role> roles;

            if (search != null && !search.trim().isEmpty()) {
                roles = roleService.searchRolesByDisplayName(search).stream()
                        .collect(java.util.stream.Collectors.toList())
                        .stream()
                        .collect(java.util.stream.Collectors.collectingAndThen(
                                java.util.stream.Collectors.toList(),
                                list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                        ));
            } else {
                roles = roleService.getActiveRoles(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", roles.getContent());
            response.put("totalElements", roles.getTotalElements());
            response.put("totalPages", roles.getTotalPages());
            response.put("currentPage", roles.getNumber());
            response.put("pageSize", roles.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取角色列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取所有启用的角色（不分页）
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllActiveRoles() {
        try {
            java.util.List<Role> roles = roleService.getAllActiveRoles();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", roles);
            response.put("count", roles.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取角色列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{roleId}")
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable Long roleId) {
        try {
            Optional<Role> role = roleService.getRoleById(roleId);

            if (role.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", role.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "角色不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取角色详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 检查角色名称是否唯一
     */
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Object>> checkRoleNameUnique(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {

        try {
            boolean isUnique = roleService.isRoleNameUnique(name, excludeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("isUnique", isUnique);
            response.put("data", dataMap);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "检查角色名称失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

