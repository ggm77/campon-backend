package com.seohamin.campon.domain.post.dto;

import com.seohamin.campon.domain.post.entity.Post;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long id,
        Long campsiteId,
        String title,
        String content,
        LocalDateTime createdAt
) {

    public static PostResponseDto of(final Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getCampsiteId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt()
        );
    }
}
