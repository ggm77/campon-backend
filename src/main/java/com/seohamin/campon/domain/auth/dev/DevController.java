package com.seohamin.campon.domain.auth.dev;

import com.seohamin.campon.domain.user.entity.User;
import com.seohamin.campon.domain.user.repository.UserRepository;
import com.seohamin.campon.global.auth.jwt.JwtProvider;
import com.seohamin.campon.global.constant.Role;
import com.seohamin.campon.global.dto.JwtDto;
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
}
