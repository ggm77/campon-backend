package com.seohamin.campon.domain.campsite.score;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampsiteScoreCalculatorTest {

    // 고정 점수를 반환하는 테스트용 정책
    private record FixedPolicy(String nameValue, double weightValue, double scoreValue) implements ScorePolicy {
        @Override
        public String name() {
            return nameValue;
        }

        @Override
        public double weight() {
            return weightValue;
        }

        @Override
        public double score(final CampsiteProfile profile, final ScoreContext context) {
            return scoreValue;
        }
    }

    @Test
    @DisplayName("정책들의 가중 평균을 0~100 정수로 환산한다")
    void weightedAverage() {
        // 가중치 30에 1.0점 + 가중치 70에 0.5점 = (30 + 35) / 100 = 65점
        final CampsiteScoreCalculator calculator = new CampsiteScoreCalculator(List.of(
                new FixedPolicy("a", 30, 1.0),
                new FixedPolicy("b", 70, 0.5)
        ));

        assertThat(calculator.calculate(null, null).total()).isEqualTo(65);
    }

    @Test
    @DisplayName("항목별 점수를 이름/0~100 점수/가중치와 함께 반환한다")
    void policyScoreBreakdown() {
        final CampsiteScoreCalculator calculator = new CampsiteScoreCalculator(List.of(
                new FixedPolicy("a", 30, 1.0),
                new FixedPolicy("b", 70, 0.5)
        ));

        final ScoreResult result = calculator.calculate(null, null);

        assertThat(result.policyScores()).containsExactly(
                new ScoreResult.PolicyScore("a", 100, 30),
                new ScoreResult.PolicyScore("b", 50, 70)
        );
    }

    @Test
    @DisplayName("모든 정책이 만점이면 100점, 0점이면 0점이다")
    void boundaries() {
        assertThat(new CampsiteScoreCalculator(List.of(new FixedPolicy("a", 50, 1.0)))
                .calculate(null, null).total()).isEqualTo(100);
        assertThat(new CampsiteScoreCalculator(List.of(new FixedPolicy("a", 50, 0.0)))
                .calculate(null, null).total()).isEqualTo(0);
    }

    @Test
    @DisplayName("정책이 없으면 0점이고 내역도 비어있다")
    void noPolicies() {
        final ScoreResult result = new CampsiteScoreCalculator(List.of()).calculate(null, null);

        assertThat(result.total()).isEqualTo(0);
        assertThat(result.policyScores()).isEmpty();
    }
}