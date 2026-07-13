package com.seohamin.campon.domain.campsite.fixture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seohamin.campon.global.infra.tourApi.dto.NearbyApiResponseDto;

import java.util.HashMap;
import java.util.Map;

/**
 * 테스트용 GoCamping API Item 생성 헬퍼.
 * 필드가 80개가 넘는 record라 Jackson으로 필요한 필드만 채워 생성한다 (나머지는 null).
 */
public final class CampsiteItemFixture {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private CampsiteItemFixture() { }

    public static NearbyApiResponseDto.Item item(final Map<String, String> overrides) {
        final Map<String, String> fields = new HashMap<>(Map.of(
                "contentId", "1",
                "facltNm", "테스트 캠핑장",
                "mapX", "127.0",
                "mapY", "37.5"
        ));
        fields.putAll(overrides);
        return MAPPER.convertValue(fields, NearbyApiResponseDto.Item.class);
    }
}