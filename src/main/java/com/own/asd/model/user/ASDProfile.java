package com.own.asd.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "asd_profiles")
@Data
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class ASDProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "allergic_foods", columnDefinition = "TEXT")
    private String allergicFoods;

    @Column(name = "intolerant_foods", columnDefinition = "TEXT")
    private String intolerantFoods;

    @Column(name = "sensory_sensitivity", length = 50)
    private String sensorySensitivity;

    @Column(name = "behavior_patterns", columnDefinition = "TEXT")
    private String behaviorPatterns;

    @Enumerated(EnumType.STRING)
    private SensoryPreference colorPreference;

    @Enumerated(EnumType.STRING)
    private SensoryPreference texturePreference;

    @Enumerated(EnumType.STRING)
    private SensoryPreference smellPreference;

    @Column(name = "eating_behavior_notes", columnDefinition = "TEXT")
    private String eatingBehaviorNotes;

    @Column(name = "special_notes", columnDefinition = "TEXT")
    private String specialNotes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    @JsonIgnore
    private Child child;

    public enum SensoryPreference {
        LIKE, DISLIKE, NEUTRAL, SENSITIVE, AVERSIVE;

        @JsonValue
        public String toValue() {
            return this.name();
        }
    }

    // Helper methods for compatibility with existing code
    public String getSensorySensitivity() {
        return sensorySensitivity;
    }

    public String getBehaviorPatterns() {
        return behaviorPatterns;
    }

    public List<String> getAllergies() {
        if (allergicFoods == null || allergicFoods.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(allergicFoods.split(","));
    }
}

