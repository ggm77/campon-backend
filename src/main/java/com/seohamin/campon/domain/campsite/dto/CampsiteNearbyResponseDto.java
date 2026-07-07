package com.seohamin.campon.domain.campsite.dto;

import java.util.List;

public record CampsiteNearbyResponseDto(
        Long campsiteId,
        String name,
        String lineIntro,
        String description,
        Double lat,
        Double lon,
        Integer distance,
        String zipcode,
        String tel,
        String resveUrl,
        List<String> facility,
        String thumbnailUrl,
        Boolean trailerAccompanyAt,
        Boolean caravanAccompanyAt,
        Integer toiletCount,
        Integer showerRoomCount,
        Integer sinkCount,
        List<String> equipmentRental
) { }
