package com.seohamin.campon.domain.post.dto;

public record PostRequestDto(
        Long campsiteId,
        String title,
        String content
) {
}
