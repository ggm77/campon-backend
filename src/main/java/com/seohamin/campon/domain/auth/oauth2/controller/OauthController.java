package com.seohamin.campon.domain.auth.oauth2.controller;

import com.seohamin.campon.domain.auth.oauth2.dto.OauthRequestDto;
import com.seohamin.campon.global.dto.JwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class OauthController {

    // 애플 OAuth API
    @PostMapping("/oauth2/apple")
    public ResponseEntity<JwtDto> appleOauth2(
            @RequestBody final OauthRequestDto oauthRequestDto
    ) {

        // mock
        return ResponseEntity.ok(new JwtDto(
                "test",
                "Bearer",
                1L,
                "test"
        ));
    }

    // 구글 OAuth API
    @PostMapping("/oauth2/google")
    public ResponseEntity<JwtDto> googleOauth2(
            @RequestBody final OauthRequestDto oauthRequestDto
    ) {

        // mock
        return ResponseEntity.ok(new JwtDto(
                "test",
                "Bearer",
                1L,
                "test"
        ));
    }
}
