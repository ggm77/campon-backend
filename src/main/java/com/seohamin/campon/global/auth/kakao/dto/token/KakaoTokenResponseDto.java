package com.seohamin.campon.global.auth.kakao.dto.token;

import lombok.Getter;

@Getter
public class KakaoTokenResponseDto {
    private String token_type;
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private Integer refresh_token_expires_in;
    private String scope;
    private String error;
    private String error_description;
}