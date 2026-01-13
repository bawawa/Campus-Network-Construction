package com.own.asd.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.own.asd.model.nutrition.DietaryRestriction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "children")
@Data
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "diagnosis_info", columnDefinition = "TEXT")
    private String diagnosisInfo;

    @Column(name = "allergy_history", columnDefinition = "TEXT")
    private String allergyHistory;

    @Column(name = "preferred_foods", columnDefinition = "TEXT")
    private String preferredFoods;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    @JsonIgnore
    private User parent;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DietaryRestriction> dietaryRestrictions;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ASDProfile> asdProfiles;

    public enum Gender {
        MALE, FEMALE
    }

    // 计算年龄
    public Integer getAge() {
        if (birthDate == null) return null;
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    // 获取最新的ASD档案
    public ASDProfile getAsdProfile() {
        if (asdProfiles == null || asdProfiles.isEmpty()) {
            return null;
        }
        return asdProfiles.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .orElse(null);
    }
}

