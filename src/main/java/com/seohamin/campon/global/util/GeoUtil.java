package com.seohamin.campon.global.util;

import org.springframework.stereotype.Component;

@Component
public class GeoUtil {

    private static final double EARTH_RADIUS_METERS = 6_371_000;

    /**
     * 위도 경도로 두 지점 사이의 거리 계산하는 메서드 (Haversine)
     * @param lat1 지점1 위도
     * @param lon1 지점1 경도
     * @param lat2 지점2 위도
     * @param lon2 지점2 경도
     * @return 거리 (m 단위, 좌표 정보 하나라도 없으면 null)
     */
    public static Integer calculateDistance(
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
}