package com.seohamin.campon.global.auth.kakao.dto.user;

import lombok.Getter;

@Getter
public class KakaoUserInfoResponseDto {
    private Long id;
    private KakaoAccountDto kakao_account;

    @Getter
    public static class KakaoAccountDto {
        private String email;
        private KakaoProfileDto profile;
    }

    @Getter
    public static class KakaoProfileDto {
        private String nickname;
        private String profile_image_url;
    }
}