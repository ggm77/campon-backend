package com.seohamin.campon.domain.auth.token.service;

import com.seohamin.campon.domain.auth.token.dto.TokenRefreshRequestDto;
import com.seohamin.campon.domain.user.entity.User;
import com.seohamin.campon.domain.user.repository.UserRepository;
import com.seohamin.campon.global.auth.jwt.JwtProvider;
import com.seohamin.campon.global.dto.JwtDto;
import com.seohamin.campon.global.exception.CustomException;
import com.seohamin.campon.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    /**
     * 토큰 리프레시하는 메서드
     * @param tokenRefreshRequestDto 리프레시 토큰 담긴 DTO
     * @return JWT
     */
    public JwtDto tokenRefresh(final TokenRefreshRequestDto tokenRefreshRequestDto) {
        // 1) null 검사
        if (
                tokenRefreshRequestDto == null
                || tokenRefreshRequestDto.refreshToken() == null
                || tokenRefreshRequestDto.refreshToken().isBlank()
        ) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        // 2) 토큰 추출
        final String refreshToken = tokenRefreshRequestDto.refreshToken();

        // 3) 토큰 검증 (리프레시 토큰이 맞는지 확인)
        final String userIdStr = jwtProvider.getRefreshTokenSubject(refreshToken);

        // 4) 유저 아이디 파싱
        final Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException ex) {
            throw new CustomException(ExceptionCode.INVALID_TOKEN);
        }

        // 5) 유저 조회
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        // 6) 토큰 생성 및 리턴
        return new JwtDto(
                jwtProvider.creatAccessToken(userId, user.getRole()),
                jwtProvider.getTokenType(),
                jwtProvider.getAccessTokenExpirationTime(),
                jwtProvider.creatRefreshToken(userId)
        );
    }
}
