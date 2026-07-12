package com.seohamin.campon.global.auth.kakao.client;

import com.seohamin.campon.global.auth.kakao.dto.token.KakaoTokenResponseDto;
import com.seohamin.campon.global.auth.kakao.dto.user.KakaoUserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthClient {

    @Value("${oauth2.kakao.token_uri}")
    private String KAKAO_TOKEN_URI;

    @Value("${oauth2.kakao.user_info_uri}")
    private String KAKAO_USER_INFO_URI;

    @Value("${oauth2.kakao.rest_api_key}")
    private String KAKAO_REST_API_KEY;

    @Value("${oauth2.kakao.client_secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${oauth2.kakao.redirect_url}")
    private String KAKAO_REDIRECT_URI;

    private final WebClient kakaoAuthWebClient;
    private final WebClient kakaoApiWebClient;

    // https://kauth.kakao.com/oauth/token에 code를 보내 토큰을 받아온다.
    public KakaoTokenResponseDto requestToken(final String authorizationCode) {
        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", KAKAO_REST_API_KEY);
        formData.add("client_secret", KAKAO_CLIENT_SECRET);
        formData.add("redirect_uri", KAKAO_REDIRECT_URI);
        formData.add("code", authorizationCode);

        return kakaoAuthWebClient.post()
                .uri(KAKAO_TOKEN_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            log.error("Kakao Token API Error: {}", body);
                            return Mono.error(new RuntimeException("Kakao Login Failed: " + body));
                        })
                )
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();
    }

    // https://kapi.kakao.com/v2/user/me에 access token으로 유저 정보를 받아온다.
    public KakaoUserInfoResponseDto requestUserInfo(final String accessToken) {
        return kakaoApiWebClient.get()
                .uri(KAKAO_USER_INFO_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            log.error("Kakao User Info API Error: {}", body);
                            return Mono.error(new RuntimeException("Kakao User Info Failed: " + body));
                        })
                )
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();
    }
}