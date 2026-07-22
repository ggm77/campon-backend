package com.seohamin.campon.domain.direction.service;

import com.seohamin.campon.domain.direction.dto.DirectionResponseDto;
import com.seohamin.campon.global.exception.CustomException;
import com.seohamin.campon.global.exception.constants.ExceptionCode;
import com.seohamin.campon.global.infra.kakaoMobility.KakaoMobilityClient;
import com.seohamin.campon.global.infra.kakaoMobility.dto.KakaoDirectionsApiResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectionServiceTest {

    @Mock
    private KakaoMobilityClient kakaoMobilityClient;

    private DirectionService service;

    @BeforeEach
    void setUp() {
        service = new DirectionService(kakaoMobilityClient);
    }

    // sections 파라미터의 각 원소가 하나의 section(road 1개)이 되도록 조립하는 헬퍼
    private static KakaoDirectionsApiResponseDto response(
            final int resultCode,
            final int distance,
            final int duration,
            final List<List<Double>> sections
    ) {
        final List<KakaoDirectionsApiResponseDto.Section> sectionList = sections.stream()
                .map(vertexes -> new KakaoDirectionsApiResponseDto.Section(
                        List.of(new KakaoDirectionsApiResponseDto.Road(vertexes))
                ))
                .toList();

        return new KakaoDirectionsApiResponseDto(
                "trans-id",
                List.of(new KakaoDirectionsApiResponseDto.Route(
                        resultCode,
                        "OK",
                        new KakaoDirectionsApiResponseDto.Summary(distance, duration),
                        sectionList
                ))
        );
    }

    private void givenApiReturns(final KakaoDirectionsApiResponseDto apiResponse) {
        when(kakaoMobilityClient.getCarDirections(anyDouble(), anyDouble(), anyDouble(), anyDouble(), any()))
                .thenReturn(apiResponse);
    }

    @Test
    @DisplayName("여러 section/road에 걸친 vertexes를 순서대로 펼쳐 distance/duration과 함께 반환한다")
    void mapsResponseInOrder() {
        givenApiReturns(response(0, 5230, 720, List.of(
                List.of(127.10, 37.39, 127.11, 37.40),
                List.of(127.12, 37.41)
        )));

        final DirectionResponseDto result = service.getDirections(127.0, 37.3, 127.2, 37.5, null);

        assertThat(result.distance()).isEqualTo(5230);
        assertThat(result.duration()).isEqualTo(720);
        assertThat(result.path()).containsExactly(
                new DirectionResponseDto.Point(127.10, 37.39),
                new DirectionResponseDto.Point(127.11, 37.40),
                new DirectionResponseDto.Point(127.12, 37.41)
        );
    }

    @Test
    @DisplayName("필수 좌표가 없으면 INVALID_REQUEST 예외가 발생한다")
    void missingCoordinate() {
        assertThatThrownBy(() -> service.getDirections(null, 37.3, 127.2, 37.5, null))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("위도/경도가 범위를 벗어나면 INVALID_REQUEST 예외가 발생한다")
    void outOfRangeCoordinate() {
        assertThatThrownBy(() -> service.getDirections(127.0, 91.0, 127.2, 37.5, null))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("경유지 포맷이 잘못되면 INVALID_REQUEST 예외가 발생한다")
    void invalidWaypointsFormat() {
        assertThatThrownBy(() -> service.getDirections(127.0, 37.3, 127.2, 37.5, "127.1,37.4,extra"))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("경유지가 5개를 넘으면 INVALID_REQUEST 예외가 발생한다")
    void tooManyWaypoints() {
        final String waypoints = "127.1,37.1|127.2,37.2|127.3,37.3|127.4,37.4|127.5,37.5|127.6,37.6";

        assertThatThrownBy(() -> service.getDirections(127.0, 37.3, 127.2, 37.5, waypoints))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("카카오 응답의 result_code가 0이 아니면 ROUTE_NOT_FOUND 예외가 발생한다")
    void routeNotFound() {
        givenApiReturns(response(104, 0, 0, List.of()));

        assertThatThrownBy(() -> service.getDirections(127.0, 37.3, 127.2, 37.5, null))
                .isInstanceOf(CustomException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.ROUTE_NOT_FOUND);
    }
}