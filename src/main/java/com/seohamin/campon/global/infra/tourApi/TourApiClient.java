package com.seohamin.campon.global.infra.tourApi;

import com.seohamin.campon.global.infra.tourApi.dto.NearbyApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class TourApiClient {

    @Value("${tour_api.mobile_os}")
    private String MOBILE_OS;

    @Value("${tour_api.mobile_app}")
    private String MOBILE_APP;

    @Value("${tour_api.service_key}")
    private String SERVICE_KEY;

    private final WebClient tourApiWebClient;

    /**
     * /locationBasedList에 요청 넣는 메서드
     * @param lat 위도
     * @param lon 경도
     * @param radius 범위 (20,000 제한)
     * @param size 페이지 크기
     * @param page 페이지 번호
     * @return TourAPI 응답 그대로의 DTO
     */
    public NearbyApiResponseDto getCampsiteNearby(
            final double lat,
            final double lon,
            final Integer radius,
            final Integer size,
            final Integer page
    ) {
        return tourApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/locationBasedList")
                        .queryParam("serviceKey", SERVICE_KEY)
                        .queryParam("MobileOS", MOBILE_OS)
                        .queryParam("MobileApp", MOBILE_APP)
                        .queryParam("_type", "json")
                        .queryParam("mapX", lon)
                        .queryParam("mapY", lat)
                        .queryParam("radius", radius)
                        .queryParam("numOfRows", size)
                        .queryParam("pageNo", page)
                        .build())
                .retrieve()
                .bodyToMono(NearbyApiResponseDto.class)
                .block();
    }
}