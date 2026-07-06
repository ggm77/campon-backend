package com.seohamin.campon.domain.user.controller;

import com.seohamin.campon.domain.user.dto.UserRequestDto;
import com.seohamin.campon.domain.user.dto.UserResponseDto;
import com.seohamin.campon.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    // 유저 정보 Upsert하는 API
    @PutMapping("/users")
    public ResponseEntity<UserResponseDto> upsertUser(
            @AuthenticationPrincipal final String userIdStr,
            @RequestBody final UserRequestDto userRequestDto
    ) {

        return ResponseEntity.ok(userService.upsertUser(userIdStr, userRequestDto));
    }

    // 유저 정보 조회 API
    @GetMapping("/users")
    public ResponseEntity<UserResponseDto> getUser() {

        // mock
        return ResponseEntity.ok(
                new UserResponseDto(
                        true,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
    }

    // 유저 탈퇴 API
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUser() {

        // mock
        return ResponseEntity.noContent().build();
    }
}
