package com.own.asd.repository;

import com.own.asd.model.nutrition.NutritionistNote;
import com.own.asd.model.user.Child;
import com.own.asd.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NutritionistNoteRepository extends JpaRepository<NutritionistNote, Long> {

    /**
     * 根据儿童ID查找营养师留言
     */
    List<NutritionistNote> findByChildIdOrderByCreatedAtDesc(Long childId);

    /**
     * 根据儿童ID分页查找营养师留言
     */
    Page<NutritionistNote> findByChildIdOrderByCreatedAtDesc(Long childId, Pageable pageable);

    /**
     * 根据营养师ID查找其创建的所有留言
     */
    List<NutritionistNote> findByNutritionistIdOrderByCreatedAtDesc(Long nutritionistId);

    /**
     * 根据营养师ID分页查找其创建的所有留言
     */
    Page<NutritionistNote> findByNutritionistIdOrderByCreatedAtDesc(Long nutritionistId, Pageable pageable);

    /**
     * 查找未读的留言
     */
    List<NutritionistNote> findByChildIdAndIsReadFalseOrderByCreatedAtDesc(Long childId);

    /**
     * 根据留言类型查找
     */
    List<NutritionistNote> findByChildIdAndNoteTypeOrderByCreatedAtDesc(Long childId, String noteType);

    /**
     * 统计某个儿童的留言数量
     */
    Long countByChildId(Long childId);

    /**
     * 统计某个儿童未读留言数量
     */
    Long countByChildIdAndIsReadFalse(Long childId);

    /**
     * 根据优先级查找留言
     */
    List<NutritionistNote> findByChildIdAndPriorityGreaterThanEqualOrderByCreatedAtDesc(Long childId, Integer priority);
}

