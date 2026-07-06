package com.seohamin.campon.domain.user.dto;

import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;

import java.util.List;

public record UserRequestDto(
        Boolean hasCar,
        List<String> preferredConditions,
        List<String> equipment
) { }
