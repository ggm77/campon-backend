package com.seohamin.campon.domain.campsite.score.policy;

import com.seohamin.campon.domain.campsite.score.ScoreContext;
import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static com.seohamin.campon.domain.campsite.fixture.CampsiteProfileFixture.aProfile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class ScorePolicyTest {

    private static ScoreContext context(
            final boolean withCar,
            final Set<Facility> preferred,
            final Set<Equipment> equipments
    ) {
        return new ScoreContext(
                37.5, 127.0, 10000,
                LocalDate.of(2026, 8, 1),
                2, withCar, preferred, equipments
        );
    }

    @Nested
    class FacilityScorePolicyTest {

        private final FacilityScorePolicy policy = new FacilityScorePolicy();

        @Test
        @DisplayName("선호 시설이 없으면 만점이다")
        void noPreferenceIsFullScore() {
            assertThat(policy.score(aProfile().build(), context(false, Set.of(), Set.of())))
                    .isEqualTo(1.0);
        }

        @Test
        @DisplayName("선호 시설 중 충족한 비율만큼 점수를 준다")
        void partialMatch() {
            final var profile = aProfile()
                    .facilities(Set.of(Facility.ELECTRICITY))
                    .build();
            final var ctx = context(false, Set.of(Facility.ELECTRICITY, Facility.SHOWER), Set.of());

            assertThat(policy.score(profile, ctx)).isEqualTo(0.5);
        }
    }

    @Nested
    class EquipmentRentalScorePolicyTest {

        private final EquipmentRentalScorePolicy policy = new EquipmentRentalScorePolicy();

        @Test
        @DisplayName("기본 장비를 전부 보유했으면 만점이다")
        void fullyEquippedIsFullScore() {
            final var ctx = context(false, Set.of(), Set.of(
                    Equipment.TENT, Equipment.TARP, Equipment.SLEEPING_BAG, Equipment.SLEEPING_PAD,
                    Equipment.PORTABLE_STOVE, Equipment.COOKWARE, Equipment.LANTERN,
                    Equipment.CAMPING_TABLE_CHAIR
            ));

            assertThat(policy.score(aProfile().build(), ctx)).isEqualTo(1.0);
        }

        @Test
        @DisplayName("부족한 장비 중 대여 가능한 비율만큼 점수를 준다")
        void rentableCoverage() {
            // 기본 장비 8종 중 6종 보유 → 부족분 TENT, TARP 중 TENT만 대여 가능 → 0.5
            final var profile = aProfile()
                    .rentalEquipments(Set.of(Equipment.TENT))
                    .build();
            final var ctx = context(false, Set.of(), Set.of(
                    Equipment.SLEEPING_BAG, Equipment.SLEEPING_PAD, Equipment.PORTABLE_STOVE,
                    Equipment.COOKWARE, Equipment.LANTERN, Equipment.CAMPING_TABLE_CHAIR
            ));

            assertThat(policy.score(profile, ctx)).isEqualTo(0.5);
        }
    }

    @Nested
    class DistanceScorePolicyTest {

        private final DistanceScorePolicy policy = new DistanceScorePolicy();

        @Test
        @DisplayName("가까울수록 높고 반경 끝에서 0점이다")
        void linearDecay() {
            final var ctx = context(false, Set.of(), Set.of());

            assertThat(policy.score(aProfile().distance(0).build(), ctx)).isEqualTo(1.0);
            assertThat(policy.score(aProfile().distance(5000).build(), ctx))
                    .isCloseTo(0.5, offset(1e-9));
            assertThat(policy.score(aProfile().distance(10000).build(), ctx)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("거리 정보가 없으면 0점이다")
        void nullDistanceIsZero() {
            assertThat(policy.score(aProfile().distance(null).build(), context(false, Set.of(), Set.of())))
                    .isEqualTo(0.0);
        }
    }

    @Nested
    class SiteTypeScorePolicyTest {

        private final SiteTypeScorePolicy policy = new SiteTypeScorePolicy();

        @Test
        @DisplayName("해당되는 이동 수단 조건이 없으면 만점이다")
        void noConditionIsFullScore() {
            assertThat(policy.score(aProfile().build(), context(false, Set.of(), Set.of())))
                    .isEqualTo(1.0);
        }

        @Test
        @DisplayName("차량 동반인데 오토캠핑 사이트가 없으면 0점이다")
        void carWithoutAutoSite() {
            assertThat(policy.score(aProfile().autoSiteCount(0).build(), context(true, Set.of(), Set.of())))
                    .isEqualTo(0.0);
            assertThat(policy.score(aProfile().autoSiteCount(5).build(), context(true, Set.of(), Set.of())))
                    .isEqualTo(1.0);
        }

        @Test
        @DisplayName("여러 조건이면 충족 비율로 점수를 준다")
        void mixedConditions() {
            // 차량 O(오토사이트 있음) + 카라반 O(허용 안 됨) → 1/2
            final var profile = aProfile().autoSiteCount(5).build();
            final var ctx = context(true, Set.of(), Set.of(Equipment.CARAVAN));

            assertThat(policy.score(profile, ctx)).isEqualTo(0.5);
        }
    }

    @Nested
    class SafetyScorePolicyTest {

        private final SafetyScorePolicy policy = new SafetyScorePolicy();

        @Test
        @DisplayName("보험 가입이 절반, 소방 설비가 절반을 차지한다")
        void insuranceAndFireSafety() {
            final var ctx = context(false, Set.of(), Set.of());

            assertThat(policy.score(aProfile().build(), ctx)).isEqualTo(0.0);
            assertThat(policy.score(aProfile().insured(true).build(), ctx)).isEqualTo(0.5);
            assertThat(policy.score(aProfile().insured(true).fireSafetyCount(4).build(), ctx))
                    .isEqualTo(1.0);
            // 상한(4개) 초과는 만점과 동일
            assertThat(policy.score(aProfile().insured(true).fireSafetyCount(100).build(), ctx))
                    .isEqualTo(1.0);
        }
    }
}