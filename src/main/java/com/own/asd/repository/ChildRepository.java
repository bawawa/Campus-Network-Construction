package com.own.asd.repository;

import com.own.asd.model.user.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

    List<Child> findByParentId(Long parentId);

    List<Child> findByParentIdAndIsActiveTrue(Long parentId);

    Optional<Child> findByIdAndParentId(Long id, Long parentId);

    @Query("SELECT c FROM Child c WHERE c.parent.id = :parentId AND c.isActive = true ORDER BY c.createdAt DESC")
    List<Child> findActiveChildrenByParent(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Child c JOIN c.asdProfiles p WHERE c.id = :childId AND c.isActive = true")
    Optional<Child> findChildWithASDProfile(@Param("childId") Long childId);

    boolean existsByIdAndParentId(Long id, Long parentId);
}

