package com.seohamin.campon.domain.auth.dev;

import com.seohamin.campon.domain.user.entity.User;
import com.seohamin.campon.domain.user.repository.UserRepository;
import com.seohamin.campon.global.auth.jwt.JwtProvider;
import com.seohamin.campon.global.constant.Role;
import com.seohamin.campon.global.dto.JwtDto;
import com.seohamin.campon.global.infra.gemini.GeminiClient;
import com.seohamin.campon.global.infra.kakaoMobility.KakaoMobilityClient;
import com.seohamin.campon.global.infra.kakaoMobility.dto.KakaoDirectionsApiResponseDto;
import com.seohamin.campon.global.infra.tourApi.TourApiClient;
import com.seohamin.campon.global.infra.tourApi.dto.NearbyApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class DevController {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final TourApiClient tourApiClient;
    private final GeminiClient geminiClient;
    private final KakaoMobilityClient kakaoMobilityClient;

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

    @PostMapping("/dev/user")
    public ResponseEntity<Void> createUser() {

        final User user = User.builder()
                .role(Role.USER)
                .hasCar(false)
                .preferredConditions(new HashSet<>())
                .equipments(new HashSet<>())
                .build();

        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dev/tourApiTest")
    public ResponseEntity<NearbyApiResponseDto> tourApiTest() {

        return ResponseEntity.ok(tourApiClient.getCampsiteNearby(35.26141, 129.08860, 20000, 10, 1));
    }

    @GetMapping("/dev/geminiTest")
    public ResponseEntity<String> geminiTest(
            @RequestParam final String message
    ) {
        return ResponseEntity.ok(geminiClient.chat(message));
    }

    @GetMapping("/dev/kakaoMobilityTest")
    public ResponseEntity<KakaoDirectionsApiResponseDto> kakaoMobilityTest() {

        // 서울시청 -> 강남역
        return ResponseEntity.ok(kakaoMobilityClient.getCarDirections(126.9784, 37.5665, 127.0276, 37.4979, null));
    }
}
