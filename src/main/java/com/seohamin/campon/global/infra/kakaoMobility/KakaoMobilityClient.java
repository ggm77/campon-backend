package com.seohamin.campon.global.infra.kakaoMobility;

import com.seohamin.campon.global.exception.CustomException;
import com.seohamin.campon.global.exception.constants.ExceptionCode;
import com.seohamin.campon.global.infra.kakaoMobility.dto.KakaoDirectionsApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoMobilityClient {

    @Value("${kakao_mobility.rest_api_key}")
    private String REST_API_KEY;

    @Value("${kakao_mobility.car_directions_uri}")
    private String CAR_DIRECTIONS_URI;

    private final WebClient kakaoMobilityWebClient;

    /**
     * 카카오모빌리티 자동차 길찾기(/v1/directions) 요청 메서드
     * @param originX 출발지 경도
     * @param originY 출발지 위도
     * @param destX 도착지 경도
     * @param destY 도착지 위도
     * @param waypoints 경유지 ("x,y|x,y" 형식, 없으면 null)
     * @return 카카오 응답 그대로의 DTO
     */
    public KakaoDirectionsApiResponseDto getCarDirections(
            final double originX,
            final double originY,
            final double destX,
            final double destY,
            final String waypoints
    ) {
        return kakaoMobilityWebClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(CAR_DIRECTIONS_URI)
                            .queryParam("origin", originX + "," + originY)
                            .queryParam("destination", destX + "," + destY);
                    if (waypoints != null && !waypoints.isBlank()) {
                        uriBuilder.queryParam("waypoints", waypoints);
                    }
                    return uriBuilder.build();
                })
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + REST_API_KEY)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class).flatMap(body -> {
                            log.error("Kakao Mobility Directions API Error: {}", body);
                            return Mono.error(new CustomException(ExceptionCode.KAKAO_MOBILITY_REQUEST_ERROR));
                        })
                )
                .bodyToMono(KakaoDirectionsApiResponseDto.class)
                .block();
    }
}