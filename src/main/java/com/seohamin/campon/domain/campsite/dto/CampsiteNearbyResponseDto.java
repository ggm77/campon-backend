package com.seohamin.campon.domain.campsite.dto;

import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;

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
        List<Facility> facility,
        String thumbnailUrl,
        Boolean trailerAccompanyAt,
        Boolean caravanAccompanyAt,
        Integer toiletCount,
        Integer showerRoomCount,
        Integer sinkCount,
        List<Equipment> equipmentRental
) { }
