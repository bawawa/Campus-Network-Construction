package com.own.asd.repository;

import com.own.asd.model.system.Dictionary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    /**
     * 根据类型和代码查找
     */
    Optional<Dictionary> findByTypeAndCode(String type, String code);

    /**
     * 根据类型查找所有启用的字典项
     */
    List<Dictionary> findByTypeAndIsActiveTrueOrderBySortOrderAsc(String type);

    /**
     * 分页查找指定类型的字典项
     */
    Page<Dictionary> findByTypeAndIsActiveTrueOrderBySortOrderAsc(String type, Pageable pageable);

    /**
     * 根据父ID查找子字典项
     */
    List<Dictionary> findByParentIdAndIsActiveTrueOrderBySortOrderAsc(Long parentId);

    /**
     * 查找指定类型下的根字典项（没有父级的）
     */
    List<Dictionary> findByTypeAndParentIdIsNullAndIsActiveTrueOrderBySortOrderAsc(String type);

    /**
     * 根据显示名称模糊查找
     */
    List<Dictionary> findByDisplayNameContainingIgnoreCaseAndIsActiveTrue(String displayName);

    /**
     * 检查指定类型下代码是否已存在
     */
    boolean existsByTypeAndCodeAndIdNot(String type, String code, Long id);

    /**
     * 查找所有字典类型
     */
    @Query("SELECT DISTINCT d.type FROM Dictionary d WHERE d.isActive = true")
    List<String> findAllTypes();

    /**
     * 查找所有启用的字典项
     */
    List<Dictionary> findByIsActiveTrueOrderBySortOrderAsc();

    /**
     * 根据类型和父ID查找
     */
    List<Dictionary> findByTypeAndParentIdAndIsActiveTrueOrderBySortOrderAsc(String type, Long parentId);
}

