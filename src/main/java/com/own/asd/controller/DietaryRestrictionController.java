package com.own.asd.controller;

import com.own.asd.model.nutrition.DietaryRestriction;
import com.own.asd.service.DietaryRestrictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/children/{childId}/dietary-restrictions")
@RequiredArgsConstructor
public class DietaryRestrictionController {

    private final DietaryRestrictionService dietaryRestrictionService;

    /**
     * 获取儿童的所有饮食限制
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDietaryRestrictions(@PathVariable Long childId) {
        try {
            List<DietaryRestriction> restrictions = dietaryRestrictionService.getByChildId(childId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", restrictions);
            response.put("count", restrictions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取饮食限制失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 添加饮食限制
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addDietaryRestriction(
            @PathVariable Long childId,
            @Valid @RequestBody DietaryRestriction restriction) {
        try {
            DietaryRestriction created = dietaryRestrictionService.create(childId, restriction);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "饮食限制添加成功");
            response.put("data", created);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "添加饮食限制失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新饮食限制
     */
    @PutMapping("/{restrictionId}")
    public ResponseEntity<Map<String, Object>> updateDietaryRestriction(
            @PathVariable Long childId,
            @PathVariable Long restrictionId,
            @Valid @RequestBody DietaryRestriction restriction) {
        try {
            DietaryRestriction updated = dietaryRestrictionService.update(childId, restrictionId, restriction);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "饮食限制更新成功");
            response.put("data", updated);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新饮食限制失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除饮食限制
     */
    @DeleteMapping("/{restrictionId}")
    public ResponseEntity<Map<String, Object>> deleteDietaryRestriction(
            @PathVariable Long childId,
            @PathVariable Long restrictionId) {
        try {
            dietaryRestrictionService.delete(childId, restrictionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "饮食限制删除成功");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除饮食限制失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

