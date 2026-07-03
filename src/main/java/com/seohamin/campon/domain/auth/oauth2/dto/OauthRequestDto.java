package com.seohamin.campon.domain.auth.oauth2.dto;

public record OauthRequestDto(
        String code,
        String name
) { }
