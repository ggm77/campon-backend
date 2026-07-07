package com.seohamin.campon.domain.campsite.service;

import com.seohamin.campon.domain.campsite.dto.CampsiteNearbyResponseDto;
import com.seohamin.campon.global.dto.PageNationDto;
import com.seohamin.campon.global.exception.CustomException;
import com.seohamin.campon.global.exception.constants.ExceptionCode;
import com.seohamin.campon.global.infra.tourApi.TourApiClient;
import com.seohamin.campon.global.infra.tourApi.dto.NearbyApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CampsiteService {

    private static final double EARTH_RADIUS_METERS = 6_371_000;

    private final TourApiClient tourApiClient;

    /**
     * 주변 캠핑장 목록 조회하는 메서드
     * @param lat 조회 위도
     * @param lon 조회 경도
     * @param radius 조회 반경 (m단위, 최대 20,000m)
     * @param size 페이지 크기
     * @param page 페이지 번호 (0번부터 시작)
     * @return 페이지네이션 적용된 DTO
     */
    public PageNationDto<CampsiteNearbyResponseDto> getCampsiteNearby(
            final Double lat,
            final Double lon,
            final Integer radius,
            final Integer size,
            final Integer page
    ) {
        // 1) 파라미터 검사
        if (
                lat == null || lon == null || radius == null || size == null || page == null
                || radius > 20000 || radius < 0
        ) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }
        if (size < 1 || page < 0 || size > 100) {
            throw new CustomException(ExceptionCode.INVALID_PAGING_PARAMETER);
        }

        // 2) TourAPI 호출
        final NearbyApiResponseDto apiResult = tourApiClient.getCampsiteNearby(lat, lon, radius, size, page + 1);

        // 3) 응답 변환
        return toPageNationDto(apiResult, lat, lon);
    }

    // 페이지네이션 적용하는 메서드
    private PageNationDto<CampsiteNearbyResponseDto> toPageNationDto(
            final NearbyApiResponseDto apiResult,
            final double lat,
            final double lon
    ) {
        // 1) 응답 결과에서 아이템들 리스트로 추출
        final NearbyApiResponseDto.Body body = apiResult.response().body();
        final List<NearbyApiResponseDto.Item> items = body.items() == null || body.items().item() == null
                ? List.of()
                : body.items().item();

        // 2) 커스텀 DTO 리스트로 변환
        final List<CampsiteNearbyResponseDto> content = items.stream()
                .map(item -> toCampsiteNearbyResponseDto(item, lat, lon))
                .toList();

        // 3) 다음 페이지 존재 하는지 판단
        final boolean hasNext = (long) body.pageNo() * body.numOfRows() < body.totalCount();

        return new PageNationDto<>(hasNext, content);
    }

    // DTO 변환하는 메서드
    private CampsiteNearbyResponseDto toCampsiteNearbyResponseDto(
            final NearbyApiResponseDto.Item item,
            final double lat,
            final double lon
    ) {
        // 1) 아이템의 위도 경도 정보를 파싱 (정보 없으면 null)
        final Double itemLat = parseDouble(item.mapY());
        final Double itemLon = parseDouble(item.mapX());

        return new CampsiteNearbyResponseDto(
                Long.parseLong(item.contentId()),
                item.facltNm(),
                item.lineIntro(),
                item.intro(),
                itemLat,
                itemLon,
                calculateDistance(lat, lon, itemLat, itemLon), // 거리 계산
                item.zipcode(),
                item.tel(),
                item.resveUrl(),
                splitCsv(item.sbrsCl()), // 문자열을 리스트로 변환
                item.firstImageUrl(),
                "Y".equals(item.trlerAcmpnyAt()),
                "Y".equals(item.caravAcmpnyAt()),
                parseInt(item.toiletCo()),
                parseInt(item.swrmCo()),
                parseInt(item.wtrplCo()),
                splitCsv(item.eqpmnLendCl()) // 문자열을 리스트로 변환
        );
    }

    // 위도 경도로 거리 계산하는 메서드
    private Integer calculateDistance(
            final Double lat1,
            final Double lon1,
            final Double lat2,
            final Double lon2
    ) {
        // 1) 위도 경도 정보 하나라도 없으면 null 반환
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return null;
        }

        // 2) 거리 계산
        final double dLat = Math.toRadians(lat2 - lat1);
        final double dLon = Math.toRadians(lon2 - lon1);
        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) Math.round(EARTH_RADIUS_METERS * c);
    }

    // 쉼표가 구분자인 문자열을 리스트로 변환하는 메서드
    private List<String> splitCsv(final String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .toList();
    }

    private Integer parseInt(final String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        return Integer.parseInt(value.trim());
    }

    private Double parseDouble(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Double.parseDouble(value.trim());
    }

}
