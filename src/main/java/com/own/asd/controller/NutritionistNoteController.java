package com.own.asd.controller;

import com.own.asd.model.nutrition.NutritionistNote;
import com.own.asd.service.NutritionistNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/nutritionist-notes")
public class NutritionistNoteController {

    @Autowired
    private NutritionistNoteService noteService;

    /**
     * 创建营养师留言
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNote(
            @RequestParam @NotNull Long childId,
            @RequestParam @NotNull Long nutritionistId,
            @RequestParam @NotNull String noteType,
            @RequestParam @NotNull String title,
            @RequestParam @NotNull String content,
            @RequestParam(required = false) String referenceLinks,
            @RequestParam(defaultValue = "3") Integer priority) {

        try {
            NutritionistNote note = noteService.createNote(
                    childId, nutritionistId, noteType, title, content, referenceLinks, priority);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "留言创建成功");
            response.put("data", note);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "留言创建失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取儿童的所有留言
     */
    @GetMapping("/child/{childId}")
    public ResponseEntity<Map<String, Object>> getNotesByChildId(
            @PathVariable Long childId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<NutritionistNote> notes = noteService.getNotesByChildId(childId, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notes.getContent());
            response.put("totalElements", notes.getTotalElements());
            response.put("totalPages", notes.getTotalPages());
            response.put("currentPage", notes.getNumber());
            response.put("pageSize", notes.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取留言失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取营养师的留言
     */
    @GetMapping("/nutritionist/{nutritionistId}")
    public ResponseEntity<Map<String, Object>> getNotesByNutritionistId(
            @PathVariable Long nutritionistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<NutritionistNote> notes = noteService.getNotesByNutritionistId(nutritionistId, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notes.getContent());
            response.put("totalElements", notes.getTotalElements());
            response.put("totalPages", notes.getTotalPages());
            response.put("currentPage", notes.getNumber());
            response.put("pageSize", notes.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取留言失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取儿童的未读留言
     */
    @GetMapping("/child/{childId}/unread")
    public ResponseEntity<Map<String, Object>> getUnreadNotes(@PathVariable Long childId) {
        try {
            List<NutritionistNote> notes = noteService.getUnreadNotesByChildId(childId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notes);
            response.put("count", notes.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取未读留言失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据留言类型获取留言
     */
    @GetMapping("/child/{childId}/type/{noteType}")
    public ResponseEntity<Map<String, Object>> getNotesByType(
            @PathVariable Long childId,
            @PathVariable String noteType) {

        try {
            List<NutritionistNote> notes = noteService.getNotesByChildIdAndType(childId, noteType);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", notes);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取留言失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 标记留言为已读
     */
    @PutMapping("/{noteId}/mark-read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long noteId) {
        try {
            NutritionistNote note = noteService.markAsRead(noteId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "留言已标记为已读");
            response.put("data", note);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "标记已读失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 家长回复留言
     */
    @PutMapping("/{noteId}/response")
    public ResponseEntity<Map<String, Object>> addParentResponse(
            @PathVariable Long noteId,
            @RequestParam @NotNull String responseText) {

        try {
            NutritionistNote note = noteService.addParentResponse(noteId, responseText);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "回复成功");
            response.put("data", note);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "回复失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新留言
     */
    @PutMapping("/{noteId}")
    public ResponseEntity<Map<String, Object>> updateNote(
            @PathVariable Long noteId,
            @RequestParam(required = false) String noteType,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String referenceLinks,
            @RequestParam(required = false) Integer priority) {

        try {
            NutritionistNote note = noteService.updateNote(
                    noteId, noteType, title, content, referenceLinks, priority);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "留言更新成功");
            response.put("data", note);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "留言更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除留言
     */
    @DeleteMapping("/{noteId}")
    public ResponseEntity<Map<String, Object>> deleteNote(@PathVariable Long noteId) {
        try {
            noteService.deleteNote(noteId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "留言删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "留言删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取留言统计信息
     */
    @GetMapping("/child/{childId}/stats")
    public ResponseEntity<Map<String, Object>> getNoteStats(@PathVariable Long childId) {
        try {
            Long totalNotes = noteService.countNotesByChildId(childId);
            Long unreadNotes = noteService.countUnreadNotesByChildId(childId);
            List<NutritionistNote> highPriorityNotes = noteService.getHighPriorityNotes(childId, 4);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("totalNotes", totalNotes);
            dataMap.put("unreadNotes", unreadNotes);
            dataMap.put("highPriorityNotes", highPriorityNotes.size());
            response.put("data", dataMap);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取统计信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取留言详情
     */
    @GetMapping("/{noteId}")
    public ResponseEntity<Map<String, Object>> getNoteById(@PathVariable Long noteId) {
        try {
            Optional<NutritionistNote> note = noteService.getNoteById(noteId);

            if (note.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", note.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "留言未找到");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取留言详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取营养师统计数据
     */
    @GetMapping("/nutritionist/{nutritionistId}/stats")
    public ResponseEntity<Map<String, Object>> getNutritionistStats(@PathVariable Long nutritionistId) {
        try {
            Map<String, Object> stats = noteService.getNutritionistStats(nutritionistId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "统计数据获取成功");
            response.put("data", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取统计数据失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

