package com.seohamin.campon.domain.user.dto;

import com.seohamin.campon.domain.user.entity.User;
import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;

import java.util.ArrayList;
import java.util.List;

public record UserResponseDto(
        Boolean hasCar,
        List<Facility> preferredConditions,
        List<Equipment> equipment
) {

    public static UserResponseDto of(User user) {
        return new UserResponseDto(
                user.getHasCar(),
                new ArrayList<>(user.getPreferredConditions()),
                new ArrayList<>(user.getEquipments())
        );
    }
}
