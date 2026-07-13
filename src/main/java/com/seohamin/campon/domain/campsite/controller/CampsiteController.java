package com.seohamin.campon.domain.campsite.controller;

import com.seohamin.campon.domain.campsite.dto.CampsiteNearbyResponseDto;
import com.seohamin.campon.domain.campsite.dto.CampsiteRecommendResponseDto;
import com.seohamin.campon.domain.campsite.service.CampsiteRecommendService;
import com.seohamin.campon.domain.campsite.service.CampsiteService;
import com.seohamin.campon.global.dto.PageNationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CampsiteController {

    private final CampsiteService campsiteService;
    private final CampsiteRecommendService campsiteRecommendService;

    // 주변 캠핑장 조회 API
    @GetMapping("/campsites/nearby")
    public ResponseEntity<PageNationDto<CampsiteNearbyResponseDto>> getCampsiteNearby(
            @RequestParam final Double lat,
            @RequestParam final Double lon,
            @RequestParam final Integer radius,
            @RequestParam final Integer size,
            @RequestParam final Integer page
    ) {

        return ResponseEntity.ok(campsiteService.getCampsiteNearby(lat, lon, radius, size, page));
    }

    // 캠핑장 추천 API
    @GetMapping("/campsites/recommend")
    public ResponseEntity<PageNationDto<CampsiteRecommendResponseDto>> getCampsiteRecommend(
            @RequestParam final Double lat,
            @RequestParam final Double lon,
            @RequestParam final Integer radius,
            @RequestParam final LocalDateTime date,
            @RequestParam final Integer groupSize,
            @RequestParam final Boolean withCar,
            @RequestParam final List<String> preferredConditions,
            @RequestParam final List<String> equipments,
            @RequestParam final Integer size,
            @RequestParam final Integer page
    ) {

        return ResponseEntity.ok(campsiteRecommendService.getCampsiteRecommend(
                lat, lon, radius, date, groupSize, withCar, preferredConditions, equipments, size, page
        ));
    }
}