package com.seohamin.campon.domain.campsite.mapper;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;
import com.seohamin.campon.global.infra.tourApi.dto.NearbyApiResponseDto;
import com.seohamin.campon.global.util.GeoUtil;
import com.seohamin.campon.global.util.ParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class CampsiteProfileMapper {

    private static final DateTimeFormatter[] CLOSED_DATE_FORMATS = {
            DateTimeFormatter.ISO_LOCAL_DATE,   // yyyy-MM-dd
            DateTimeFormatter.BASIC_ISO_DATE    // yyyyMMdd
    };

    /**
     * GoCamping API 아이템을 정규화된 CampsiteProfile로 변환하는 메서드
     * @param item API 응답 아이템
     * @param requestLat 요청 기준 위도 (거리 계산용)
     * @param requestLon 요청 기준 경도 (거리 계산용)
     * @return 정규화된 프로필
     */
    public CampsiteProfile toProfile(
            final NearbyApiResponseDto.Item item,
            final double requestLat,
            final double requestLon
    ) {
        // 1) 좌표 파싱 및 거리 계산
        final Double lat = ParseUtil.parseDoubleOrNull(item.mapY());
        final Double lon = ParseUtil.parseDoubleOrNull(item.mapX());

        // 2) 소방 설비 개수 합산
        final int fireSafetyCount = ParseUtil.parseIntOrZero(item.extshrCo())
                + ParseUtil.parseIntOrZero(item.frprvtWrppCo())
                + ParseUtil.parseIntOrZero(item.frprvtSandCo())
                + ParseUtil.parseIntOrZero(item.fireSensorCo());

        return new CampsiteProfile(
                Long.parseLong(item.contentId()),
                item.facltNm(),
                item.lineIntro(),
                item.intro(),
                lat,
                lon,
                GeoUtil.calculateDistance(requestLat, requestLon, lat, lon),
                item.zipcode(),
                item.tel(),
                item.resveUrl(),
                item.firstImageUrl(),
                mapFacilities(item),
                mapRentalEquipments(item.eqpmnLendCl()),
                ParseUtil.parseIntOrZero(item.gnrlSiteCo()),
                ParseUtil.parseIntOrZero(item.autoSiteCo()),
                ParseUtil.parseIntOrZero(item.glampSiteCo()),
                ParseUtil.parseIntOrZero(item.caravSiteCo())
                        + ParseUtil.parseIntOrZero(item.indvdlCaravSiteCo()),
                "Y".equals(item.trlerAcmpnyAt()),
                "Y".equals(item.caravAcmpnyAt()),
                ParseUtil.parseIntOrZero(item.toiletCo()),
                ParseUtil.parseIntOrZero(item.swrmCo()),
                ParseUtil.parseIntOrZero(item.wtrplCo()),
                "Y".equals(item.insrncAt()),
                fireSafetyCount,
                isOperating(item.manageSttus()),
                parseDateOrNull(item.hvofBgnde()),
                parseDateOrNull(item.hvofEnddle()),
                ParseUtil.splitCsv(item.operPdCl())
        );
    }

    // 부대시설 문자열(sbrsCl)과 파생 필드들을 Facility 집합으로 정규화하는 메서드
    private Set<Facility> mapFacilities(final NearbyApiResponseDto.Item item) {
        final Set<Facility> facilities = EnumSet.noneOf(Facility.class);

        // 1) sbrsCl 한글 토큰 매핑 (매핑표에 없는 토큰은 로그만 남기고 무시)
        for (final String token : ParseUtil.splitCsv(item.sbrsCl())) {
            final Optional<Facility> mapped = Facility.fromApiToken(token);
            if (mapped.isPresent()) {
                facilities.add(mapped.get());
            } else {
                log.debug("매핑되지 않은 부대시설 토큰: {} (contentId={})", token, item.contentId());
            }
        }

        // 2) 개수 필드에서 시설 존재 여부 파생
        if (ParseUtil.parseIntOrZero(item.swrmCo()) > 0) {
            facilities.add(Facility.SHOWER);
        }
        if (ParseUtil.parseIntOrZero(item.toiletCo()) > 0) {
            facilities.add(Facility.TOILET);
        }
        if (ParseUtil.parseIntOrZero(item.wtrplCo()) > 0) {
            facilities.add(Facility.SINK);
        }

        // 3) 애완동물 동반 여부 ("가능", "가능(소형견)" 등)
        if (item.animalCmgCl() != null && item.animalCmgCl().startsWith("가능")) {
            facilities.add(Facility.PET_FRIENDLY);
        }

        // 4) 화로대 사용 가능 여부 ("개별", "공동" 등, "불가"만 제외)
        final String brazier = item.brazierCl();
        if (brazier != null && !brazier.isBlank() && !brazier.contains("불가")) {
            facilities.add(Facility.BONFIRE_PIT);
        }

        return facilities;
    }

    // 대여장비 문자열(eqpmnLendCl)을 Equipment 집합으로 정규화하는 메서드
    private Set<Equipment> mapRentalEquipments(final String eqpmnLendCl) {
        final Set<Equipment> equipments = EnumSet.noneOf(Equipment.class);
        for (final String token : ParseUtil.splitCsv(eqpmnLendCl)) {
            final Optional<Equipment> mapped = Equipment.fromApiToken(token);
            if (mapped.isPresent()) {
                equipments.add(mapped.get());
            } else {
                log.debug("매핑되지 않은 대여장비 토큰: {}", token);
            }
        }
        return equipments;
    }

    // 운영 상태 판단하는 메서드 (데이터 누락이 많아 blank는 운영 중으로 간주)
    private boolean isOperating(final String manageSttus) {
        return manageSttus == null || manageSttus.isBlank() || manageSttus.contains("운영");
    }

    // 휴장일 문자열을 LocalDate로 파싱하는 메서드 (형식이 일정하지 않아 실패 시 null)
    private LocalDate parseDateOrNull(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (final DateTimeFormatter format : CLOSED_DATE_FORMATS) {
            try {
                return LocalDate.parse(value.trim(), format);
            } catch (DateTimeParseException ignored) {
                // 다음 형식으로 재시도
            }
        }
        return null;
    }
}