package com.seohamin.campon.domain.campsite.score.policy;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import com.seohamin.campon.domain.campsite.score.ScoreContext;
import com.seohamin.campon.domain.campsite.score.ScorePolicy;
import com.seohamin.campon.global.constant.Facility;
import org.springframework.stereotype.Component;

/**
 * 유저가 선호하는 시설을 캠핑장이 얼마나 갖추고 있는지 평가하는 정책
 */
@Component
public class FacilityScorePolicy implements ScorePolicy {

    private static final double WEIGHT = 35;

    @Override
    public String name() {
        return "facility";
    }

    @Override
    public double weight() {
        return WEIGHT;
    }

    @Override
    public double score(final CampsiteProfile profile, final ScoreContext context) {
        // 1) 선호 시설이 없으면 만점
        if (context.preferredConditions().isEmpty()) {
            return 1.0;
        }

        // 2) 선호 시설 중 충족 비율 계산
        final long matched = context.preferredConditions().stream()
                .filter(profile.facilities()::contains)
                .count();
        return (double) matched / context.preferredConditions().size();
    }
}