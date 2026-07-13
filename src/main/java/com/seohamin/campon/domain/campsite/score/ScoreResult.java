package com.seohamin.campon.domain.campsite.score;

import java.util.List;

/**
 * 최종 점수와 항목별 점수 내역
 * @param total 가중 평균 총점 (0~100)
 * @param policyScores 항목별 점수 목록
 */
public record ScoreResult(
        int total,
        List<PolicyScore> policyScores
) {

    /**
     * 항목 하나의 점수
     * @param name 항목 이름 (ScorePolicy.name())
     * @param score 해당 항목의 0~100 점수
     * @param weight 총점에 반영되는 가중치
     */
    public record PolicyScore(
            String name,
            int score,
            double weight
    ) { }
}