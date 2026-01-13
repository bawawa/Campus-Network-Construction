package com.own.asd.repository;

import com.own.asd.model.system.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色名称查找
     */
    Optional<Role> findByName(String name);

    /**
     * 查找所有启用的角色
     */
    List<Role> findByIsActiveTrueOrderBySortOrderAsc();

    /**
     * 分页查找启用的角色
     */
    Page<Role> findByIsActiveTrueOrderBySortOrderAsc(Pageable pageable);

    /**
     * 根据显示名称模糊查找
     */
    List<Role> findByDisplayNameContainingIgnoreCaseAndIsActiveTrue(String displayName);

    /**
     * 检查角色名称是否已存在
     */
    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * 根据权限查找角色
     */
    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.code = :permissionCode AND r.isActive = true")
    List<Role> findByPermissionCode(@Param("permissionCode") String permissionCode);
}

