package com.seohamin.campon.domain.auth.oauth2.service;

import com.seohamin.campon.domain.auth.oauth2.dto.OauthRequestDto;
import com.seohamin.campon.domain.user.dto.UserOauthAccountsRequestDto;
import com.seohamin.campon.domain.user.dto.UserOauthAccountsResponseDto;
import com.seohamin.campon.domain.user.service.UserOauthService;
import com.seohamin.campon.global.auth.apple.client.AppleAuthClient;
import com.seohamin.campon.global.auth.apple.dto.key.ApplePublicKeyResponseDto;
import com.seohamin.campon.global.auth.apple.dto.token.AppleTokenResponseDto;
import com.seohamin.campon.global.auth.apple.util.AppleKeyGenerator;
import com.seohamin.campon.global.auth.jwt.JwtProvider;
import com.seohamin.campon.global.constant.Role;
import com.seohamin.campon.global.dto.JwtDto;
import com.seohamin.campon.global.exception.CustomException;
import com.seohamin.campon.global.exception.constants.ExceptionCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final AppleKeyGenerator appleKeyGenerator;
    private final AppleAuthClient appleAuthClient;
    private final JwtProvider jwtProvider;
    private final UserOauthService userOauthService;

    /**
     * Apple OAuth2 진행하는 메서드
     * @param oauthRequestDto 사용자 이름과 auth code 담긴 DTO
     * @return JWT
     */
    public JwtDto processAppleOauth(final OauthRequestDto oauthRequestDto) {
        // 1) null 검사
        if (
                oauthRequestDto == null || oauthRequestDto.code() == null || oauthRequestDto.code().isBlank()
        ) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        // 2) code와 name 추출
        final String code = oauthRequestDto.code();
        final String name = oauthRequestDto.name();

        // 3) idToken과 리프레시 토큰 추출
        final AppleTokenResponseDto appleTokenResponseDto = appleAuthClient.requestToken(code);
        final String idToken = appleTokenResponseDto.getId_token();
        final String appleRefreshToken = appleTokenResponseDto.getRefresh_token();

        // 4) 헤더 추출
        final Map<String, String> headers = jwtProvider.getHeaders(idToken);

        // 5) 애플에 공개키 요청
        final ApplePublicKeyResponseDto applePublicKeyResponseDto = appleAuthClient.requestKeys();

        // 6) 키 조합
        final PublicKey publicKey = appleKeyGenerator.generatePublicKey(
                headers,
                applePublicKeyResponseDto
        );

        // 7) 애플 아이디와 이메일 가져오기
        final Claims claims = jwtProvider.getClaimsFromAppleToken(idToken, publicKey);
        final String accountId = claims.getSubject();
        final String email = claims.get("email", String.class);

        // 8) oauth 정보 저장용 DTO 생성
        final UserOauthAccountsRequestDto userOauthAccountsRequestDto = new UserOauthAccountsRequestDto(
                "apple",
                accountId,
                email,
                name,
                null,
                appleRefreshToken
        );

        // 9) 유저 로그인 또는 회원가입
        final UserOauthAccountsResponseDto userOauthAccountsResponseDto = userOauthService.upsertOAuthUser(userOauthAccountsRequestDto);

        // 10) JWT 생성
        final Long userId = userOauthAccountsResponseDto.userId();
        final Role userRole = userOauthAccountsResponseDto.userRole();
        final String accessToken = jwtProvider.creatAccessToken(userId, userRole);
        final String refreshToken = jwtProvider.creatRefreshToken(userId);

        return new JwtDto(
                accessToken,
                jwtProvider.getTokenType(),
                jwtProvider.getAccessTokenExpirationTime(),
                refreshToken
        );
    }
}
