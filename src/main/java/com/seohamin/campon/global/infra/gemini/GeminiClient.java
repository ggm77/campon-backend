package com.seohamin.campon.global.infra.gemini;

import com.seohamin.campon.global.infra.gemini.dto.GeminiRequestDto;
import com.seohamin.campon.global.infra.gemini.dto.GeminiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    @Value("${gemini.api.uri}")
    private String API_URI;

    @Value("${gemini.api.key}")
    private String API_KEY;

    private final WebClient geminiApiWebClient;

    public String chat(final String message) {
        final GeminiRequestDto body = GeminiRequestDto.of(message);

        final GeminiResponseDto responseDto = geminiApiWebClient.post()
                .uri(API_URI)
                .header("x-goog-api-key", API_KEY)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(GeminiResponseDto.class)
                .block();

        return responseDto.firstText();
    }
}
