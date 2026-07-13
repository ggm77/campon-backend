package com.seohamin.campon.domain.campsite.score.policy;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import com.seohamin.campon.domain.campsite.score.ScoreContext;
import com.seohamin.campon.domain.campsite.score.ScorePolicy;
import com.seohamin.campon.global.constant.Equipment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 유저의 이동 수단(차량/트레일러/카라반)과 캠핑장 사이트 유형이 부합하는지 평가하는 정책
 */
@Component
public class SiteTypeScorePolicy implements ScorePolicy {

    private static final double WEIGHT = 15;

    @Override
    public String name() {
        return "siteType";
    }

    @Override
    public double weight() {
        return WEIGHT;
    }

    @Override
    public double score(final CampsiteProfile profile, final ScoreContext context) {
        // 1) 유저 조건별로 적용 가능한 체크 수집
        final List<Boolean> checks = new ArrayList<>();
        if (context.withCar()) {
            checks.add(profile.autoSiteCount() > 0);
        }
        if (context.equipments().contains(Equipment.TRAILER)) {
            checks.add(profile.trailerAllowed());
        }
        if (context.equipments().contains(Equipment.CARAVAN)) {
            checks.add(profile.caravanAllowed() || profile.caravanSiteCount() > 0);
        }

        // 2) 해당되는 조건이 없으면 만점
        if (checks.isEmpty()) {
            return 1.0;
        }

        // 3) 충족한 체크의 비율 계산
        final long passed = checks.stream().filter(Boolean::booleanValue).count();
        return (double) passed / checks.size();
    }
}