package com.seohamin.campon.domain.auth.token.controller;

import com.seohamin.campon.domain.auth.token.dto.TokenRefreshRequestDto;
import com.seohamin.campon.domain.auth.token.service.TokenService;
import com.seohamin.campon.global.dto.JwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/token/refresh")
    public ResponseEntity<JwtDto> refreshToken(
            @RequestBody final TokenRefreshRequestDto tokenRefreshRequestDto
    ) {

        return ResponseEntity.ok(tokenService.tokenRefresh(tokenRefreshRequestDto));
    }
}
