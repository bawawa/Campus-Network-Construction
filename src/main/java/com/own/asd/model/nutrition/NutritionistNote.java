package com.own.asd.model.nutrition;

import com.own.asd.model.user.Child;
import com.own.asd.model.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "nutritionist_notes")
@Data
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class NutritionistNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutritionist_id", nullable = false)
    private User nutritionist;

    @Column(name = "note_type")
    private String noteType; // 建议、警告、提醒等

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "reference_links", columnDefinition = "TEXT")
    private String referenceLinks; // 引用的知识库文章、食谱等链接

    @Column(name = "priority")
    private Integer priority; // 优先级 1-5

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "parent_response", columnDefinition = "TEXT")
    private String parentResponse;

    @Column(name = "response_time")
    private LocalDateTime responseTime;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

