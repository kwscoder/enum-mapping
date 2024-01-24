package com.kws.fastjson.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kongweishen
 * @date 2024-01-24 14:39
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {
    MALE(1, "man"),
    FEMALE(2, "women"),
    ;

    //@EnumValueMarker
    private final int code;
    private final String desc;
}
