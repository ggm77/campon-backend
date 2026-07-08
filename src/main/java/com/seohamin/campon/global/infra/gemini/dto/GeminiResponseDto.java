package com.seohamin.campon.global.infra.gemini.dto;

import java.util.List;

public record GeminiResponseDto(
        List<Candidate> candidates,
        PromptFeedback promptFeedback,
        UsageMetadata usageMetadata
) {

    public record Candidate(
            Content content,
            String finishReason,
            Integer index
    ) { }

    public record Content(
            List<Part> parts,
            String role
    ) { }

    public record Part(
            String text
    ) { }

    public record PromptFeedback(
            String blockReason
    ) { }

    public record UsageMetadata(
            Integer promptTokenCount,
            Integer candidatesTokenCount,
            Integer totalTokenCount
    ) { }

    public String firstText() {
        return candidates().get(0).content().parts().get(0).text();
    }
}