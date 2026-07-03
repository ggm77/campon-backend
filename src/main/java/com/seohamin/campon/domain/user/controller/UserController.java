package com.seohamin.campon.domain.user.controller;

import com.seohamin.campon.domain.user.dto.UserRequestDto;
import com.seohamin.campon.domain.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    // 유저 정보 Upsert하는 API
    @PutMapping("/users")
    public ResponseEntity<UserResponseDto> upsertUser(
            @RequestBody final UserRequestDto userRequestDto
    ) {

        // mock
        return ResponseEntity.ok(
                new UserResponseDto(
                        true,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
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
