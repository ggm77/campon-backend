package com.seohamin.campon.domain.campsite.dto;

import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;

import java.util.List;

public record CampsiteRecommendResponseDto(
        Long campsiteId,
        Integer score,
        List<ScoreDetail> scoreDetails,
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
) {

    /**
     * 항목별 점수
     * @param name 항목 이름 (facility, equipmentRental, distance, siteType, safety)
     * @param score 해당 항목의 0~100 점수
     * @param weight 총점에 반영되는 가중치
     */
    public record ScoreDetail(
            String name,
            Integer score,
            Double weight
    ) { }
}