package com.seohamin.campon.global.constant;

import java.util.Map;
import java.util.Optional;

public enum Equipment {
    TENT,               // 텐트
    TARP,               // 타프
    SLEEPING_BAG,       // 침낭
    SLEEPING_PAD,       // 매트/에어매트
    PORTABLE_STOVE,     // 버너/스토브
    COOKWARE,           // 조리도구/식기
    COOLER,             // 아이스박스
    LANTERN,            // 랜턴/조명
    CAMPING_TABLE_CHAIR,// 테이블/체어
    HEATER,             // 난방기구
    GENERATOR,          // 발전기/파워뱅크
    VEHICLE,            // 자가용 (오토캠핑 조건)
    TRAILER,            // 트레일러
    CARAVAN,            // 카라반
    TRASH_BAG,          // 쓰레기 봉투
    POWER_BANK,         // 보조 배터리
    SPARE_CLOTHES,      // 여벌 옷
    FIRST_AID_KIT;      // 구급약

    // GoCamping API 대여장비(eqpmnLendCl) 한글 토큰 매핑표
    private static final Map<String, Equipment> API_TOKEN_MAP = Map.ofEntries(
            Map.entry("텐트", TENT),
            Map.entry("타프", TARP),
            Map.entry("침낭", SLEEPING_BAG),
            Map.entry("매트", SLEEPING_PAD),
            Map.entry("에어매트", SLEEPING_PAD),
            Map.entry("버너", PORTABLE_STOVE),
            Map.entry("화로대", PORTABLE_STOVE),
            Map.entry("코펠", COOKWARE),
            Map.entry("취사도구", COOKWARE),
            Map.entry("식기", COOKWARE),
            Map.entry("아이스박스", COOLER),
            Map.entry("랜턴", LANTERN),
            Map.entry("램프", LANTERN),
            Map.entry("테이블", CAMPING_TABLE_CHAIR),
            Map.entry("의자", CAMPING_TABLE_CHAIR),
            Map.entry("난로", HEATER)
    );

    /**
     * GoCamping API의 한글 토큰을 Equipment로 변환하는 메서드
     * @param token API 응답의 대여장비 토큰 (예: "텐트")
     * @return 매핑되는 Equipment (매핑표에 없으면 empty)
     */
    public static Optional<Equipment> fromApiToken(final String token) {
        if (token == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(API_TOKEN_MAP.get(token.trim()));
    }
}