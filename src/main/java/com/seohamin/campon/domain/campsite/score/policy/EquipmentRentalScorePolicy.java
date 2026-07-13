package com.seohamin.campon.domain.campsite.score.policy;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import com.seohamin.campon.domain.campsite.score.ScoreContext;
import com.seohamin.campon.domain.campsite.score.ScorePolicy;
import com.seohamin.campon.global.constant.Equipment;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

/**
 * 유저에게 부족한 기본 장비를 캠핑장 대여로 메꿀 수 있는지 평가하는 정책
 */
@Component
public class EquipmentRentalScorePolicy implements ScorePolicy {

    private static final double WEIGHT = 20;

    // 캠핑에 필요한 기본 장비 세트 (이 중 유저가 없는 것이 "부족분")
    private static final Set<Equipment> BASIC_EQUIPMENTS = EnumSet.of(
            Equipment.TENT,
            Equipment.TARP,
            Equipment.SLEEPING_BAG,
            Equipment.SLEEPING_PAD,
            Equipment.PORTABLE_STOVE,
            Equipment.COOKWARE,
            Equipment.LANTERN,
            Equipment.CAMPING_TABLE_CHAIR
    );

    @Override
    public String name() {
        return "equipmentRental";
    }

    @Override
    public double weight() {
        return WEIGHT;
    }

    @Override
    public double score(final CampsiteProfile profile, final ScoreContext context) {
        // 1) 기본 장비 중 유저가 보유하지 않은 부족분 계산
        final Set<Equipment> missing = EnumSet.copyOf(BASIC_EQUIPMENTS);
        missing.removeAll(context.equipments());

        // 2) 부족한 게 없으면 만점
        if (missing.isEmpty()) {
            return 1.0;
        }

        // 3) 부족분 중 대여로 메꿀 수 있는 비율 계산
        final long rentable = missing.stream()
                .filter(profile.rentalEquipments()::contains)
                .count();
        return (double) rentable / missing.size();
    }
}