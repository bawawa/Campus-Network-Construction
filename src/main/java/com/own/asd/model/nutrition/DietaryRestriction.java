package com.own.asd.model.nutrition;

import com.own.asd.model.user.Child;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dietary_restrictions")
@Data
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class DietaryRestriction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestrictionType restrictionType;

    @Column(name = "food_name", nullable = false, length = 200)
    private String foodName;

    @Column(name = "severity_level")
    private Integer severityLevel; // 1-5级严重程度

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum RestrictionType {
        ALLERGY, INTOLERANCE, AVERSION, RELIGIOUS, MEDICAL, PREFERENCE
    }
}

