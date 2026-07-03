package com.seohamin.campon.domain.user.dto;

import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;

import java.util.List;

public record UserResponseDto(
        Boolean hasCar,
        List<Facility> preferredConditions,
        List<Equipment> equipment
) {
}
