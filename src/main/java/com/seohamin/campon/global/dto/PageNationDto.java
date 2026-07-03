package com.seohamin.campon.global.dto;

import java.util.List;

public record PageNationDto<T>(
        boolean hasNext,
        List<T> items
) { }
