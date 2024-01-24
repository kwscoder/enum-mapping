package com.kws.mybatis.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kws.annotation.EnumValueMarker;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author kongweishen
 * @date 2024-01-15 13:46
 */
@Getter
@RequiredArgsConstructor
public enum GenderEnum {
    MALE(1, "male", "男"),
    FEMALE(2, "female", "女"),
    UNKNOWN(3, "unknown", "未知"),
    ;

    @JsonValue
    private final Integer code;
    private final String name;
    private final String desc;
}
