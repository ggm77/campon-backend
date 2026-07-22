package com.seohamin.campon.domain.direction.service;

import com.seohamin.campon.domain.direction.dto.DirectionResponseDto;
import com.seohamin.campon.global.exception.CustomException;
import com.seohamin.campon.global.exception.constants.ExceptionCode;
import com.seohamin.campon.global.infra.kakaoMobility.KakaoMobilityClient;
import com.seohamin.campon.global.infra.kakaoMobility.dto.KakaoDirectionsApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectionService {

    // 카카오모빌리티 경유지 최대 개수 제한
    private static final int MAX_WAYPOINTS = 5;

    private final KakaoMobilityClient kakaoMobilityClient;

    /**
     * 출발지-목적지 사이의 자동차 경로를 조회하는 메서드
     * @param originX 출발지 경도
     * @param originY 출발지 위도
     * @param destX 도착지 경도
     * @param destY 도착지 위도
     * @param waypoints 경유지 ("x,y|x,y" 형식, 없으면 null)
     * @return 거리/소요시간/경로 좌표 DTO
     */
    public DirectionResponseDto getDirections(
            final Double originX,
            final Double originY,
            final Double destX,
            final Double destY,
            final String waypoints
    ) {
        // 1) 파라미터 검사
        validateCoordinates(originX, originY, destX, destY);
        validateWaypoints(waypoints);

        // 2) 카카오모빌리티 자동차 길찾기 호출
        final KakaoDirectionsApiResponseDto apiResult =
                kakaoMobilityClient.getCarDirections(originX, originY, destX, destY, waypoints);

        // 3) 응답 변환
        return toResponseDto(apiResult);
    }

    // 좌표 null/범위 검사하는 메서드
    private void validateCoordinates(
            final Double originX,
            final Double originY,
            final Double destX,
            final Double destY
    ) {
        if (originX == null || originY == null || destX == null || destY == null) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }
        if (!isValidLongitude(originX) || !isValidLongitude(destX)
                || !isValidLatitude(originY) || !isValidLatitude(destY)) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }
    }

    private boolean isValidLongitude(final double x) {
        return x >= -180 && x <= 180;
    }

    private boolean isValidLatitude(final double y) {
        return y >= -90 && y <= 90;
    }

    // 경유지 문자열("x,y|x,y") 포맷/개수 검사하는 메서드
    private void validateWaypoints(final String waypoints) {
        if (waypoints == null || waypoints.isBlank()) {
            return;
        }

        final String[] points = waypoints.split("\\|");
        if (points.length > MAX_WAYPOINTS) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        for (final String point : points) {
            final String[] xy = point.split(",");
            if (xy.length != 2) {
                throw new CustomException(ExceptionCode.INVALID_REQUEST);
            }
            try {
                Double.parseDouble(xy[0]);
                Double.parseDouble(xy[1]);
            } catch (NumberFormatException e) {
                throw new CustomException(ExceptionCode.INVALID_REQUEST);
            }
        }
    }

    // 카카오 응답을 커스텀 DTO로 변환하는 메서드
    private DirectionResponseDto toResponseDto(final KakaoDirectionsApiResponseDto apiResult) {
        // 카카오는 경로가 없어도 HTTP 200을 주기 때문에 result_code를 직접 확인해야 함
        if (apiResult.routes() == null || apiResult.routes().isEmpty()) {
            throw new CustomException(ExceptionCode.ROUTE_NOT_FOUND);
        }

        final KakaoDirectionsApiResponseDto.Route route = apiResult.routes().get(0);
        if (route.resultCode() == null || route.resultCode() != 0) {
            throw new CustomException(ExceptionCode.ROUTE_NOT_FOUND);
        }

        final List<DirectionResponseDto.Point> path = route.sections().stream()
                .flatMap(section -> section.roads().stream())
                .flatMap(road -> toPoints(road.vertexes()).stream())
                .toList();

        return new DirectionResponseDto(route.summary().distance(), route.summary().duration(), path);
    }

    // vertexes 평탄 배열([x1,y1,x2,y2,...])을 Point 리스트로 변환하는 메서드
    private List<DirectionResponseDto.Point> toPoints(final List<Double> vertexes) {
        final List<DirectionResponseDto.Point> points = new ArrayList<>();
        for (int i = 0; i + 1 < vertexes.size(); i += 2) {
            points.add(new DirectionResponseDto.Point(vertexes.get(i), vertexes.get(i + 1)));
        }
        return points;
    }
}