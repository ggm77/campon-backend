package com.seohamin.campon.domain.campsite.score;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;

/**
 * 캠핑장 선호도 점수 기준 하나를 담당하는 정책.
 * 구현체를 @Component로 등록하면 CampsiteScoreCalculator가 자동으로 집계에 포함한다.
 */
public interface ScorePolicy {

    /**
     * 이 기준의 식별자 (프론트로 나가는 항목별 점수의 키)
     * @return 항목 이름 (예: "facility")
     */
    String name();

    /**
     * 이 기준의 가중치
     * @return 가중치 (양수)
     */
    double weight();

    /**
     * 캠핑장이 이 기준을 얼마나 충족하는지 계산하는 메서드
     * @param profile 정규화된 캠핑장 프로필
     * @param context 유저 요청 조건
     * @return 0.0 ~ 1.0 정규화 점수
     */
    double score(CampsiteProfile profile, ScoreContext context);
}