package com.seohamin.campon.global.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ParseUtil {

    /**
     * 쉼표가 구분자인 문자열을 리스트로 변환하는 메서드
     * @param value 쉼표 구분 문자열 (예: "전기,온수")
     * @return 공백 제거된 토큰 리스트 (null/blank면 빈 리스트)
     */
    public static List<String> splitCsv(final String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .toList();
    }

    /**
     * 문자열을 int로 변환하는 메서드
     * @param value 변환할 문자열
     * @return 변환된 값 (null/blank/파싱 실패면 0)
     */
    public static int parseIntOrZero(final String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * 문자열을 Double로 변환하는 메서드
     * @param value 변환할 문자열
     * @return 변환된 값 (null/blank/파싱 실패면 null)
     */
    public static Double parseDoubleOrNull(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}