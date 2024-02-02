package com.kws.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author kws
 * @date 2024-01-24 21:46
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

    @Override
    public String toString() {
        return "GenderEnum{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
