package com.seohamin.campon.domain.direction.controller;

import com.seohamin.campon.domain.direction.dto.DirectionResponseDto;
import com.seohamin.campon.domain.direction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class DirectionController {

    private final DirectionService directionService;

    // 자동차 길찾기 API
    @GetMapping("/directions")
    public ResponseEntity<DirectionResponseDto> getDirections(
            @RequestParam final Double originX,
            @RequestParam final Double originY,
            @RequestParam final Double destX,
            @RequestParam final Double destY,
            @RequestParam(required = false) final String waypoints
    ) {

        return ResponseEntity.ok(directionService.getDirections(originX, originY, destX, destY, waypoints));
    }
}