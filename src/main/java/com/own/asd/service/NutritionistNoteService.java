package com.own.asd.service;

import com.own.asd.model.nutrition.NutritionistNote;
import com.own.asd.model.user.Child;
import com.own.asd.model.user.User;
import com.own.asd.repository.ChildRepository;
import com.own.asd.repository.NutritionistNoteRepository;
import com.own.asd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NutritionistNoteService {

    @Autowired
    private NutritionistNoteRepository noteRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建营养师留言
     */
    public NutritionistNote createNote(Long childId, Long nutritionistId, String noteType,
                                     String title, String content, String referenceLinks, Integer priority) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("儿童信息未找到"));

        User nutritionist = userRepository.findById(nutritionistId)
                .orElseThrow(() -> new RuntimeException("营养师信息未找到"));

        NutritionistNote note = new NutritionistNote();
        note.setChild(child);
        note.setNutritionist(nutritionist);
        note.setNoteType(noteType);
        note.setTitle(title);
        note.setContent(content);
        note.setReferenceLinks(referenceLinks);
        note.setPriority(priority != null ? priority : 3);
        note.setIsRead(false);

        return noteRepository.save(note);
    }

    /**
     * 获取儿童的所有留言
     */
    @Transactional(readOnly = true)
    public List<NutritionistNote> getNotesByChildId(Long childId) {
        return noteRepository.findByChildIdOrderByCreatedAtDesc(childId);
    }

    /**
     * 分页获取儿童的留言
     */
    @Transactional(readOnly = true)
    public Page<NutritionistNote> getNotesByChildId(Long childId, Pageable pageable) {
        return noteRepository.findByChildIdOrderByCreatedAtDesc(childId, pageable);
    }

    /**
     * 获取营养师创建的所有留言
     */
    @Transactional(readOnly = true)
    public List<NutritionistNote> getNotesByNutritionistId(Long nutritionistId) {
        return noteRepository.findByNutritionistIdOrderByCreatedAtDesc(nutritionistId);
    }

    /**
     * 分页获取营养师创建的留言
     */
    @Transactional(readOnly = true)
    public Page<NutritionistNote> getNotesByNutritionistId(Long nutritionistId, Pageable pageable) {
        return noteRepository.findByNutritionistIdOrderByCreatedAtDesc(nutritionistId, pageable);
    }

    /**
     * 获取儿童的未读留言
     */
    @Transactional(readOnly = true)
    public List<NutritionistNote> getUnreadNotesByChildId(Long childId) {
        return noteRepository.findByChildIdAndIsReadFalseOrderByCreatedAtDesc(childId);
    }

    /**
     * 根据留言类型获取留言
     */
    @Transactional(readOnly = true)
    public List<NutritionistNote> getNotesByChildIdAndType(Long childId, String noteType) {
        return noteRepository.findByChildIdAndNoteTypeOrderByCreatedAtDesc(childId, noteType);
    }

    /**
     * 标记留言为已读
     */
    public NutritionistNote markAsRead(Long noteId) {
        NutritionistNote note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("留言未找到"));
        note.setIsRead(true);
        return noteRepository.save(note);
    }

    /**
     * 家长回复留言
     */
    public NutritionistNote addParentResponse(Long noteId, String response) {
        NutritionistNote note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("留言未找到"));
        note.setParentResponse(response);
        note.setResponseTime(LocalDateTime.now());
        return noteRepository.save(note);
    }

    /**
     * 更新留言
     */
    public NutritionistNote updateNote(Long noteId, String noteType, String title,
                                     String content, String referenceLinks, Integer priority) {
        NutritionistNote note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("留言未找到"));

        if (noteType != null) note.setNoteType(noteType);
        if (title != null) note.setTitle(title);
        if (content != null) note.setContent(content);
        if (referenceLinks != null) note.setReferenceLinks(referenceLinks);
        if (priority != null) note.setPriority(priority);

        return noteRepository.save(note);
    }

    /**
     * 删除留言
     */
    public void deleteNote(Long noteId) {
        noteRepository.deleteById(noteId);
    }

    /**
     * 统计儿童的留言数量
     */
    @Transactional(readOnly = true)
    public Long countNotesByChildId(Long childId) {
        return noteRepository.countByChildId(childId);
    }

    /**
     * 统计儿童的未读留言数量
     */
    @Transactional(readOnly = true)
    public Long countUnreadNotesByChildId(Long childId) {
        return noteRepository.countByChildIdAndIsReadFalse(childId);
    }

    /**
     * 获取高优先级留言
     */
    @Transactional(readOnly = true)
    public List<NutritionistNote> getHighPriorityNotes(Long childId, Integer minPriority) {
        return noteRepository.findByChildIdAndPriorityGreaterThanEqualOrderByCreatedAtDesc(
                childId, minPriority != null ? minPriority : 4);
    }

    /**
     * 获取留言详情
     */
    @Transactional(readOnly = true)
    public Optional<NutritionistNote> getNoteById(Long noteId) {
        return noteRepository.findById(noteId);
    }

    /**
     * 获取营养师统计数据
     */
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getNutritionistStats(Long nutritionistId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // 获取该营养师服务的所有儿童数量
        List<Child> children = childRepository.findByParentId(nutritionistId);
        stats.put("totalChildren", children.size());

        // 统计该营养师创建的留言总数
        List<NutritionistNote> allNotes = noteRepository.findByNutritionistIdOrderByCreatedAtDesc(nutritionistId);
        stats.put("totalNotes", allNotes.size());

        // 统计等待家长回复的留言数（有内容但家长未回复的）
        long pendingResponses = allNotes.stream()
            .filter(note -> note.getParentResponse() == null || note.getParentResponse().isEmpty())
            .count();
        stats.put("pendingResponses", pendingResponses);

        // 统计最近7天内创建的留言数
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long recentNotes = allNotes.stream()
            .filter(note -> note.getCreatedAt().isAfter(sevenDaysAgo))
            .count();
        stats.put("recentNotes", recentNotes);

        return stats;
    }
}

