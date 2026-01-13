package com.own.asd.controller;

import com.own.asd.model.user.ASDProfile;
import com.own.asd.service.ASDProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/children/{childId}/asd-profiles")
@RequiredArgsConstructor
public class ASDProfileController {

    private final ASDProfileService asdProfileService;

    @PostMapping
    public ResponseEntity<ASDProfile> createASDProfile(
            @PathVariable Long childId,
            @Valid @RequestBody ASDProfile asdProfile) {
        ASDProfile createdProfile = asdProfileService.createASDProfile(childId, asdProfile);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
    }

    @GetMapping
    public ResponseEntity<List<ASDProfile>> getASDProfilesByChild(@PathVariable Long childId) {
        List<ASDProfile> profiles = asdProfileService.getASDProfilesByChild(childId);
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ASDProfile> getASDProfile(
            @PathVariable Long childId,
            @PathVariable Long profileId) {
        return asdProfileService.getASDProfile(childId, profileId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<ASDProfile> updateASDProfile(
            @PathVariable Long childId,
            @PathVariable Long profileId,
            @Valid @RequestBody ASDProfile profileDetails) {
        ASDProfile updatedProfile = asdProfileService.updateASDProfile(childId, profileId, profileDetails);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteASDProfile(
            @PathVariable Long childId,
            @PathVariable Long profileId) {
        asdProfileService.deleteASDProfile(childId, profileId);
        return ResponseEntity.noContent().build();
    }
}

