package com.seohamin.campon.domain.direction.dto;

import java.util.List;

public record DirectionResponseDto(
        Integer distance,
        Integer duration,
        List<Point> path
) {

    public record Point(
            Double x,
            Double y
    ) { }
}