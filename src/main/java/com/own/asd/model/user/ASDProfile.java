package com.own.asd.model.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    private Child child;

    public enum SensoryPreference {
        LIKE, DISLIKE, NEUTRAL, SENSITIVE, AVERSIVE
    }
}

