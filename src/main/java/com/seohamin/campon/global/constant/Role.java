package com.seohamin.campon.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    USER("ROLE_USER", "일반 사용자");

    private String key;
    private String title;
}
