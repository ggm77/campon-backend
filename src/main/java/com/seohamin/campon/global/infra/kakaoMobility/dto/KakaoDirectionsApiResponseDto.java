package com.seohamin.campon.global.infra.kakaoMobility.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record KakaoDirectionsApiResponseDto(
        @JsonProperty("trans_id") String transId,
        List<Route> routes
) {

    public record Route(
            @JsonProperty("result_code") Integer resultCode,
            @JsonProperty("result_msg") String resultMsg,
            Summary summary,
            List<Section> sections
    ) { }

    public record Summary(
            Integer distance,
            Integer duration
    ) { }

    public record Section(
            List<Road> roads
    ) { }

    public record Road(
            List<Double> vertexes
    ) { }
}