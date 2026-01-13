package com.own.asd.controller;

import com.own.asd.model.system.Permission;
import com.own.asd.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/system/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 创建权限
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPermission(
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String permissionType,
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "0") Integer sortOrder,
            @RequestParam(defaultValue = "true") Boolean isActive) {

        try {
            Permission permission = permissionService.createPermission(
                    code, name, description, module, permissionType, parentId, sortOrder, isActive);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "权限创建成功");
            response.put("data", permission);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "权限创建失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新权限
     */
    @PutMapping("/{permissionId}")
    public ResponseEntity<Map<String, Object>> updatePermission(
            @PathVariable Long permissionId,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String permissionType,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Integer sortOrder,
            @RequestParam(required = false) Boolean isActive) {

        try {
            Permission permission = permissionService.updatePermission(
                    permissionId, code, name, description, module, permissionType, parentId, sortOrder, isActive);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "权限更新成功");
            response.put("data", permission);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "权限更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<Map<String, Object>> deletePermission(@PathVariable Long permissionId) {
        try {
            permissionService.deletePermission(permissionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "权限删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "权限删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取权限列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String permissionType) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Permission> permissions;

            if (module != null && permissionType != null) {
                List<Permission> list = permissionService.getPermissionsByModuleAndType(module, permissionType);
                permissions = new org.springframework.data.domain.PageImpl<>(list, pageable, list.size());
            } else if (module != null) {
                List<Permission> list = permissionService.getPermissionsByModule(module);
                permissions = new org.springframework.data.domain.PageImpl<>(list, pageable, list.size());
            } else if (permissionType != null) {
                List<Permission> list = permissionService.getPermissionsByType(permissionType);
                permissions = new org.springframework.data.domain.PageImpl<>(list, pageable, list.size());
            } else {
                permissions = permissionService.getActivePermissions(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", permissions.getContent());
            response.put("totalElements", permissions.getTotalElements());
            response.put("totalPages", permissions.getTotalPages());
            response.put("currentPage", permissions.getNumber());
            response.put("pageSize", permissions.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取权限列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    public ResponseEntity<Map<String, Object>> getPermissionTree() {
        try {
            List<Permission> tree = permissionService.buildPermissionTree();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", tree);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取权限树失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取权限详情
     */
    @GetMapping("/{permissionId}")
    public ResponseEntity<Map<String, Object>> getPermissionById(@PathVariable Long permissionId) {
        try {
            Optional<Permission> permission = permissionService.getPermissionById(permissionId);

            if (permission.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", permission.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "权限不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取权限详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 检查权限代码是否唯一
     */
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Object>> checkPermissionCodeUnique(
            @RequestParam String code,
            @RequestParam(required = false) Long excludeId) {

        try {
            boolean isUnique = permissionService.isPermissionCodeUnique(code, excludeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("isUnique", isUnique);
            response.put("data", dataMap);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "检查权限代码失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

