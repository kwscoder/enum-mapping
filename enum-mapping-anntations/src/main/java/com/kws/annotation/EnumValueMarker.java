package com.kws.annotation;

import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.annotation.*;

/**
 * @author kongweishen
 * @date 2024-01-24 13:59
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JsonValue
public @interface EnumValueMarker {
}
