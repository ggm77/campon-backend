package com.seohamin.campon.domain.campsite.score;

import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;

import java.time.LocalDate;
import java.util.Set;

/**
 * 점수 계산에 필요한 유저 요청 조건 묶음
 */
public record ScoreContext(
        double lat,
        double lon,
        int radius,
        LocalDate date,
        int groupSize,
        boolean withCar,
        Set<Facility> preferredConditions,
        Set<Equipment> equipments
) { }