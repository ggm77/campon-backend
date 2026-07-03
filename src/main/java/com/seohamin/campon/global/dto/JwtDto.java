package com.seohamin.campon.global.dto;

public record JwtDto(
        String accessToken,
        String tokenType,
        Long exprTime,
        String refreshToken
) { }
