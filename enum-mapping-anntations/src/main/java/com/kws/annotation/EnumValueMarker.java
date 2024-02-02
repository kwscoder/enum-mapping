package com.kws.annotation;


import java.lang.annotation.*;

/**
 * @author kws
 * @date 2024-01-24 13:59
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumValueMarker {
}
