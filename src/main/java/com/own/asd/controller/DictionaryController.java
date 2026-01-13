package com.own.asd.controller;

import com.own.asd.model.system.Dictionary;
import com.own.asd.service.DictionaryService;
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
@RequestMapping("/api/system/dictionaries")
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 创建字典项
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDictionary(
            @RequestParam String type,
            @RequestParam String code,
            @RequestParam String displayName,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") Integer sortOrder,
            @RequestParam(defaultValue = "true") Boolean isActive,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String extraData) {

        try {
            Dictionary dictionary = dictionaryService.createDictionary(
                    type, code, displayName, description, sortOrder, isActive, parentId, extraData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "字典项创建成功");
            response.put("data", dictionary);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "字典项创建失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新字典项
     */
    @PutMapping("/{dictionaryId}")
    public ResponseEntity<Map<String, Object>> updateDictionary(
            @PathVariable Long dictionaryId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer sortOrder,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String extraData) {

        try {
            Dictionary dictionary = dictionaryService.updateDictionary(
                    dictionaryId, type, code, displayName, description, sortOrder, isActive, parentId, extraData);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "字典项更新成功");
            response.put("data", dictionary);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "字典项更新失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除字典项
     */
    @DeleteMapping("/{dictionaryId}")
    public ResponseEntity<Map<String, Object>> deleteDictionary(@PathVariable Long dictionaryId) {
        try {
            dictionaryService.deleteDictionary(dictionaryId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "字典项删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "字典项删除失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取字典列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDictionaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Dictionary> dictionaries;

            if (type != null && !type.trim().isEmpty()) {
                dictionaries = dictionaryService.getDictionariesByType(type, pageable);
            } else if (search != null && !search.trim().isEmpty()) {
                List<Dictionary> list = dictionaryService.searchDictionariesByDisplayName(search);
                dictionaries = new org.springframework.data.domain.PageImpl<>(list, pageable, list.size());
            } else {
                dictionaries = new org.springframework.data.domain.PageImpl<>(
                        dictionaryService.getAllActiveDictionaries(), pageable,
                        (long) dictionaryService.getAllActiveDictionaries().size());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dictionaries.getContent());
            response.put("totalElements", dictionaries.getTotalElements());
            response.put("totalPages", dictionaries.getTotalPages());
            response.put("currentPage", dictionaries.getNumber());
            response.put("pageSize", dictionaries.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取字典列表失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据类型获取字典项
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getDictionariesByType(@PathVariable String type) {
        try {
            List<Dictionary> dictionaries = dictionaryService.getDictionariesByType(type);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dictionaries);
            response.put("count", dictionaries.size());
            response.put("type", type);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取字典项失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取字典树
     */
    @GetMapping("/tree/{type}")
    public ResponseEntity<Map<String, Object>> getDictionaryTree(@PathVariable String type) {
        try {
            List<Dictionary> tree = dictionaryService.buildDictionaryTree(type);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", tree);
            response.put("type", type);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取字典树失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取字典映射
     */
    @GetMapping("/map/{type}")
    public ResponseEntity<Map<String, Object>> getDictionaryMap(@PathVariable String type) {
        try {
            Map<String, String> map = dictionaryService.getDictionaryMap(type);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", map);
            response.put("type", type);
            response.put("count", map.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取字典映射失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取多个类型的字典映射
     */
    @GetMapping("/maps")
    public ResponseEntity<Map<String, Object>> getMultipleDictionaryMaps(
            @RequestParam List<String> types) {

        try {
            Map<String, Map<String, String>> maps = dictionaryService.getMultipleDictionaryMaps(types);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", maps);
            response.put("types", types);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取字典映射失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取所有字典类型
     */
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getAllDictionaryTypes() {
        try {
            List<String> types = dictionaryService.getAllDictionaryTypes();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", types);
            response.put("count", types.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取字典类型失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取字典项详情
     */
    @GetMapping("/{dictionaryId}")
    public ResponseEntity<Map<String, Object>> getDictionaryById(@PathVariable Long dictionaryId) {
        try {
            Optional<Dictionary> dictionary = dictionaryService.getDictionaryById(dictionaryId);

            if (dictionary.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", dictionary.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "字典项不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取字典项详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 检查字典代码是否唯一
     */
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Object>> checkDictionaryCodeUnique(
            @RequestParam String type,
            @RequestParam String code,
            @RequestParam(required = false) Long excludeId) {

        try {
            boolean isUnique = dictionaryService.isDictionaryCodeUnique(type, code, excludeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("isUnique", isUnique);
            response.put("data", dataMap);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "检查字典代码失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 辅助方法：获取所有启用的字典项
    private List<Dictionary> getAllActiveDictionaries() {
        // 这里可以添加一个方法来获取所有启用的字典项
        // 暂时返回空列表，实际使用时需要实现
        return java.util.Collections.emptyList();
    }
}

