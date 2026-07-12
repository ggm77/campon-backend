package com.seohamin.campon.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${oauth2.apple.auth_base_url}")
    private String APPLE_AUTH_BASE_URL;

    @Value("${tour_api.base_url}")
    private String TOUR_API_BASE_URL;

    @Value("${gemini.api.base_url}")
    private String GEMINI_API_BASE_URL;

    @Value("${oauth2.kakao.auth_base_url}")
    private String KAKAO_AUTH_BASE_URL;

    @Value("${oauth2.kakao.api_base_url}")
    private String KAKAO_API_BASE_URL;

    @Bean
    public WebClient appleWebClient() {
        return WebClient.builder()
                .baseUrl(APPLE_AUTH_BASE_URL)
                .build();
    }

    @Bean
    public WebClient kakaoAuthWebClient() {
        return WebClient.builder()
                .baseUrl(KAKAO_AUTH_BASE_URL)
                .build();
    }

    @Bean
    public WebClient kakaoApiWebClient() {
        return WebClient.builder()
                .baseUrl(KAKAO_API_BASE_URL)
                .build();
    }

    @Bean
    public WebClient tourApiWebClient() {
        return WebClient.builder()
                .baseUrl(TOUR_API_BASE_URL)
                .build();
    }

    @Bean
    public WebClient geminiApiWebClient() {
        return WebClient.builder()
                .baseUrl(GEMINI_API_BASE_URL)
                .build();
    }
}
