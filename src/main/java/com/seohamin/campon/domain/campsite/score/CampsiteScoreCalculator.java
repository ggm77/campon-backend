package com.seohamin.campon.domain.campsite.score;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CampsiteScoreCalculator {

    // 스프링이 모든 ScorePolicy 구현체를 자동 주입
    private final List<ScorePolicy> policies;

    /**
     * 모든 정책의 가중합으로 최종 점수와 항목별 점수를 계산하는 메서드
     * @param profile 정규화된 캠핑장 프로필
     * @param context 유저 요청 조건
     * @return 총점(0~100)과 항목별 점수(각 0~100) 내역
     */
    public ScoreResult calculate(final CampsiteProfile profile, final ScoreContext context) {
        final List<ScoreResult.PolicyScore> policyScores = new ArrayList<>();
        double weightedSum = 0;
        double totalWeight = 0;

        for (final ScorePolicy policy : policies) {
            final double score = policy.score(profile, context);
            weightedSum += score * policy.weight();
            totalWeight += policy.weight();
            policyScores.add(new ScoreResult.PolicyScore(
                    policy.name(),
                    (int) Math.round(score * 100),
                    policy.weight()
            ));
        }

        final int total = totalWeight == 0
                ? 0
                : (int) Math.round(weightedSum / totalWeight * 100);

        return new ScoreResult(total, List.copyOf(policyScores));
    }
}