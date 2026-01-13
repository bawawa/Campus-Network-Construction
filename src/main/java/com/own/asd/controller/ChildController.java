package com.own.asd.controller;

import com.own.asd.model.user.Child;
import com.own.asd.service.ChildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/children")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @PostMapping
    public ResponseEntity<Child> createChild(@Valid @RequestBody Child child) {
        Child createdChild = childService.createChild(child);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChild);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Child>> getChildrenByParent(@PathVariable Long parentId) {
        List<Child> children = childService.getChildrenByParent(parentId);
        return ResponseEntity.ok(children);
    }

    @GetMapping("/parent/{parentId}/active")
    public ResponseEntity<List<Child>> getActiveChildrenByParent(@PathVariable Long parentId) {
        List<Child> children = childService.getActiveChildrenByParent(parentId);
        return ResponseEntity.ok(children);
    }

    @GetMapping("/{id}/parent/{parentId}")
    public ResponseEntity<Child> getChildByIdAndParent(@PathVariable Long id, @PathVariable Long parentId) {
        Child child = childService.getChildByIdAndParent(id, parentId);
        return ResponseEntity.ok(child);
    }

    @GetMapping("/{id}/with-asd-profile")
    public ResponseEntity<Child> getChildWithASDProfile(@PathVariable Long id) {
        return childService.getChildWithASDProfile(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/parent/{parentId}")
    public ResponseEntity<Child> updateChild(
            @PathVariable Long id,
            @PathVariable Long parentId,
            @Valid @RequestBody Child childDetails) {
        Child updatedChild = childService.updateChild(id, parentId, childDetails);
        return ResponseEntity.ok(updatedChild);
    }

    @DeleteMapping("/{id}/parent/{parentId}")
    public ResponseEntity<Void> deactivateChild(@PathVariable Long id, @PathVariable Long parentId) {
        childService.deactivateChild(id, parentId);
        return ResponseEntity.noContent().build();
    }
}

