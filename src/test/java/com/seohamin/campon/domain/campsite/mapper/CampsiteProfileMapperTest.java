package com.seohamin.campon.domain.campsite.mapper;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static com.seohamin.campon.domain.campsite.fixture.CampsiteItemFixture.item;
import static org.assertj.core.api.Assertions.assertThat;

class CampsiteProfileMapperTest {

    private final CampsiteProfileMapper mapper = new CampsiteProfileMapper();

    private CampsiteProfile map(final Map<String, String> overrides) {
        return mapper.toProfile(item(overrides), 37.5, 127.0);
    }

    @Test
    @DisplayName("부대시설 토큰이 Facility로 매핑되고 미지 토큰은 무시된다")
    void mapFacilityTokens() {
        final CampsiteProfile profile = map(Map.of("sbrsCl", "전기,온수,이상한토큰"));

        assertThat(profile.facilities())
                .containsExactlyInAnyOrder(Facility.ELECTRICITY, Facility.HOT_WATER);
    }

    @Test
    @DisplayName("샤워실/화장실/개수대 개수에서 시설 존재가 파생된다")
    void deriveFacilitiesFromCounts() {
        final CampsiteProfile profile = map(Map.of(
                "swrmCo", "2",
                "toiletCo", "3",
                "wtrplCo", "1"
        ));

        assertThat(profile.facilities())
                .contains(Facility.SHOWER, Facility.TOILET, Facility.SINK);
        assertThat(profile.showerCount()).isEqualTo(2);
        assertThat(profile.toiletCount()).isEqualTo(3);
        assertThat(profile.sinkCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("애완동물 동반은 '가능'으로 시작하면 PET_FRIENDLY로 매핑된다")
    void mapPetFriendly() {
        assertThat(map(Map.of("animalCmgCl", "가능(소형견)")).facilities())
                .contains(Facility.PET_FRIENDLY);
        assertThat(map(Map.of("animalCmgCl", "불가능")).facilities())
                .doesNotContain(Facility.PET_FRIENDLY);
    }

    @Test
    @DisplayName("화로대는 '불가'가 아니면 BONFIRE_PIT으로 매핑된다")
    void mapBonfirePit() {
        assertThat(map(Map.of("brazierCl", "개별")).facilities())
                .contains(Facility.BONFIRE_PIT);
        assertThat(map(Map.of("brazierCl", "불가")).facilities())
                .doesNotContain(Facility.BONFIRE_PIT);
        assertThat(map(Map.of()).facilities())
                .doesNotContain(Facility.BONFIRE_PIT);
    }

    @Test
    @DisplayName("대여장비 토큰이 Equipment로 매핑된다")
    void mapRentalEquipments() {
        final CampsiteProfile profile = map(Map.of("eqpmnLendCl", "텐트,화로대,미지장비"));

        assertThat(profile.rentalEquipments())
                .containsExactlyInAnyOrder(Equipment.TENT, Equipment.PORTABLE_STOVE);
    }

    @Test
    @DisplayName("휴장일은 yyyy-MM-dd와 yyyyMMdd 형식을 모두 파싱하고 실패 시 null이다")
    void parseClosedDates() {
        assertThat(map(Map.of("hvofBgnde", "2026-08-01")).closedFrom())
                .isEqualTo(LocalDate.of(2026, 8, 1));
        assertThat(map(Map.of("hvofBgnde", "20260801")).closedFrom())
                .isEqualTo(LocalDate.of(2026, 8, 1));
        assertThat(map(Map.of("hvofBgnde", "상시")).closedFrom()).isNull();
        assertThat(map(Map.of()).closedFrom()).isNull();
    }

    @Test
    @DisplayName("운영 상태가 비어있으면 운영 중으로 간주하고 '휴장'이면 아니다")
    void mapOperatingStatus() {
        assertThat(map(Map.of()).operating()).isTrue();
        assertThat(map(Map.of("manageSttus", "운영")).operating()).isTrue();
        assertThat(map(Map.of("manageSttus", "휴장")).operating()).isFalse();
    }

    @Test
    @DisplayName("좌표가 없으면 lat/lon/distance가 null이다")
    void missingCoordinates() {
        final CampsiteProfile profile = mapper.toProfile(
                item(Map.of("mapX", "", "mapY", "")), 37.5, 127.0
        );

        assertThat(profile.lat()).isNull();
        assertThat(profile.lon()).isNull();
        assertThat(profile.distance()).isNull();
    }

    @Test
    @DisplayName("휴장 기간에 걸린 날짜는 이용 불가로 판단한다")
    void notOperableDuringClosedPeriod() {
        final CampsiteProfile profile = map(Map.of(
                "hvofBgnde", "2026-08-01",
                "hvofEnddle", "2026-08-31"
        ));

        assertThat(profile.isOperableOn(LocalDate.of(2026, 8, 15))).isFalse();
        assertThat(profile.isOperableOn(LocalDate.of(2026, 9, 1))).isTrue();
    }

    @Test
    @DisplayName("운영 계절 정보가 있으면 요청 날짜의 계절이 포함되어야 이용 가능하다")
    void notOperableOutOfSeason() {
        final CampsiteProfile profile = map(Map.of("operPdCl", "봄,여름"));

        assertThat(profile.isOperableOn(LocalDate.of(2026, 7, 1))).isTrue();
        assertThat(profile.isOperableOn(LocalDate.of(2026, 1, 15))).isFalse();
    }
}