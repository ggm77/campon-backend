package com.seohamin.campon.domain.campsite.model;

import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * GoCamping API 응답을 정규화한 도메인 모델.
 * 비정형 문자열 파싱은 CampsiteProfileMapper에서 끝나고, 점수/필터 로직은 이 모델만 소비한다.
 */
public record CampsiteProfile(
        Long contentId,
        String name,
        String lineIntro,
        String intro,
        Double lat,
        Double lon,
        Integer distance,
        String zipcode,
        String tel,
        String resveUrl,
        String thumbnailUrl,
        Set<Facility> facilities,
        Set<Equipment> rentalEquipments,
        int generalSiteCount,
        int autoSiteCount,
        int glampingSiteCount,
        int caravanSiteCount,
        boolean trailerAllowed,
        boolean caravanAllowed,
        int toiletCount,
        int showerCount,
        int sinkCount,
        boolean insured,
        int fireSafetyCount,
        boolean operating,
        LocalDate closedFrom,
        LocalDate closedTo,
        List<String> operationSeasons
) {

    /**
     * 해당 날짜에 이용 가능한 캠핑장인지 판단하는 메서드 (하드 필터용)
     * @param date 이용 예정 날짜
     * @return 운영 중이고, 휴장 기간과 운영 계절에 걸리지 않으면 true
     */
    public boolean isOperableOn(final LocalDate date) {
        // 1) 운영 상태 확인
        if (!operating) {
            return false;
        }

        // 2) 휴장 기간에 걸리는지 확인
        if (
                date != null && closedFrom != null && closedTo != null
                && !date.isBefore(closedFrom) && !date.isAfter(closedTo)
        ) {
            return false;
        }

        // 3) 운영 계절 정보가 있으면 요청 날짜의 계절 포함 여부 확인
        if (date != null && !operationSeasons.isEmpty() && !operationSeasons.contains(seasonOf(date))) {
            return false;
        }

        return true;
    }

    // 날짜가 속한 계절(GoCamping operPdCl 어휘)을 반환하는 메서드
    private static String seasonOf(final LocalDate date) {
        return switch (date.getMonthValue()) {
            case 3, 4, 5 -> "봄";
            case 6, 7, 8 -> "여름";
            case 9, 10, 11 -> "가을";
            default -> "겨울";
        };
    }
}