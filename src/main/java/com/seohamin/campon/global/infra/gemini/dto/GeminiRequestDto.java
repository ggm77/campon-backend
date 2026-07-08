package com.seohamin.campon.global.infra.gemini.dto;

import java.util.List;

public record GeminiRequestDto(
        List<Content> contents
) {

    public record Content(
            List<Part> parts
    ) { }

    public record Part(
            String text
    ) { }

    public static GeminiRequestDto of(final String message) {
        return new GeminiRequestDto(
                List.of(new Content(List.of(new Part(message))))
        );
    }
}