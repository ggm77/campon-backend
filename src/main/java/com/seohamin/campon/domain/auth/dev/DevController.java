package com.seohamin.campon.domain.auth.dev;

import com.seohamin.campon.global.auth.jwt.JwtProvider;
import com.seohamin.campon.global.constant.Role;
import com.seohamin.campon.global.dto.JwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class DevController {

    private final JwtProvider jwtProvider;

    @GetMapping("/dev/token")
    public ResponseEntity<JwtDto> getDevToken(
            @RequestParam final Long userId
    ) {
        return ResponseEntity.ok(
                new JwtDto(
                    jwtProvider.creatAccessToken(userId, Role.USER),
                    jwtProvider.getTokenType(),
                    jwtProvider.getAccessTokenExpirationTime(),
                    jwtProvider.creatRefreshToken(userId)
                )
        );
    }
}
