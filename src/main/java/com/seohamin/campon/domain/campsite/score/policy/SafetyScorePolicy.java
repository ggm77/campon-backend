package com.seohamin.campon.domain.campsite.score.policy;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import com.seohamin.campon.domain.campsite.score.ScoreContext;
import com.seohamin.campon.domain.campsite.score.ScorePolicy;
import org.springframework.stereotype.Component;

/**
 * 보험 가입 여부와 소방 설비 보유량으로 안전 수준을 평가하는 정책
 */
@Component
public class SafetyScorePolicy implements ScorePolicy {

    private static final double WEIGHT = 10;

    // 소방 설비 개수 정규화 상한 (이 이상은 동일하게 만점 처리)
    private static final int FIRE_SAFETY_CAP = 4;

    @Override
    public String name() {
        return "safety";
    }

    @Override
    public double weight() {
        return WEIGHT;
    }

    @Override
    public double score(final CampsiteProfile profile, final ScoreContext context) {
        final double insuranceScore = profile.insured() ? 0.5 : 0.0;
        final double fireSafetyScore =
                (double) Math.min(profile.fireSafetyCount(), FIRE_SAFETY_CAP) / FIRE_SAFETY_CAP * 0.5;
        return insuranceScore + fireSafetyScore;
    }
}