package com.seohamin.campon.domain.post.entity;

import com.seohamin.campon.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    @NotNull
    private Long campsiteId;

    @Column(length = 100, nullable = false)
    @NotNull
    @Size(max = 100)
    private String title;

    @Column(length = 2048, nullable = false)
    @NotNull
    @Size(max = 2048)
    private String content;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Post(
            final User author,
            final Long campsiteId,
            final String title,
            final String content
    ) {
        this.author = author;
        this.campsiteId = campsiteId;
        this.title = title;
        this.content = content;
    }

    public void updateCampsiteId(final Long campsiteId) {
        this.campsiteId = campsiteId;
    }

    public void updateTitle(final String title) {
        this.title = title;
    }
    public void updateContent(final String content) {
        this.content = content;
    }
}
