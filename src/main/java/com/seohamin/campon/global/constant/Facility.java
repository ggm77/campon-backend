package com.seohamin.campon.global.constant;

import java.util.Map;
import java.util.Optional;

public enum Facility {
    SHOWER,          // 샤워실
    TOILET,          // 화장실
    SINK,            // 개수대
    ELECTRICITY,     // 전기
    WIFI,            // 와이파이
    HOT_WATER,       // 온수
    FIREWOOD_SALE,   // 장작 판매
    WATER_PLAY,      // 물놀이장
    PLAYGROUND,      // 놀이터
    EXERCISE_FACILITY, // 운동시설
    PET_FRIENDLY,    // 애완동물
    BONFIRE_PIT;     // 모닥불 피우는 공간

    // GoCamping API 부대시설(sbrsCl) 한글 토큰 매핑표
    private static final Map<String, Facility> API_TOKEN_MAP = Map.ofEntries(
            Map.entry("전기", ELECTRICITY),
            Map.entry("무선인터넷", WIFI),
            Map.entry("온수", HOT_WATER),
            Map.entry("장작판매", FIREWOOD_SALE),
            Map.entry("물놀이장", WATER_PLAY),
            Map.entry("놀이터", PLAYGROUND),
            Map.entry("운동시설", EXERCISE_FACILITY),
            Map.entry("운동장", EXERCISE_FACILITY)
    );

    /**
     * GoCamping API의 한글 토큰을 Facility로 변환하는 메서드
     * @param token API 응답의 부대시설 토큰 (예: "전기")
     * @return 매핑되는 Facility (매핑표에 없으면 empty)
     */
    public static Optional<Facility> fromApiToken(final String token) {
        if (token == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(API_TOKEN_MAP.get(token.trim()));
    }
}