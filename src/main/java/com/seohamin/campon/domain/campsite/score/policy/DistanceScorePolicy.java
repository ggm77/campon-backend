package com.seohamin.campon.domain.campsite.score.policy;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import com.seohamin.campon.domain.campsite.score.ScoreContext;
import com.seohamin.campon.domain.campsite.score.ScorePolicy;
import org.springframework.stereotype.Component;

/**
 * 요청 위치에서 가까울수록 높게 평가하는 정책
 */
@Component
public class DistanceScorePolicy implements ScorePolicy {

    private static final double WEIGHT = 20;

    @Override
    public String name() {
        return "distance";
    }

    @Override
    public double weight() {
        return WEIGHT;
    }

    @Override
    public double score(final CampsiteProfile profile, final ScoreContext context) {
        // 1) 거리 정보가 없거나 반경이 0이면 0점
        if (profile.distance() == null || context.radius() <= 0) {
            return 0.0;
        }

        // 2) 반경 대비 거리 비율로 선형 감소 (0~1로 클램프)
        final double ratio = 1.0 - (double) profile.distance() / context.radius();
        return Math.max(0.0, Math.min(1.0, ratio));
    }
}