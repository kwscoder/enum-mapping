package com.kws.annotation;

import com.fasterxml.jackson.annotation.JsonValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kws
 * @date 2024-01-12 18:12
 */
public class EnumValueMarkerFinder {
//    public static final Class<? extends Annotation> ANNOTATION_CLASS = EnumValueMarker.class;
    public static final Class<? extends Annotation> ANNOTATION_CLASS = JsonValue.class;

    public static boolean hasAnnotation(Class<?> clazz) {
        try {
            return hasAnnotation(clazz, ANNOTATION_CLASS);
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        try {
            return findAnnotatedFields(clazz, annotationClass).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static List<Field> findAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        if (!clazz.isEnum()) {
            throw new RuntimeException("Class " + clazz.getName() + " is not an Enum");
        }
        return Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(annotationClass)).collect(Collectors.toList());
    }

    public static Field find(Class<?> clazz) {
        return find(clazz, ANNOTATION_CLASS);
    }

    public static Field find(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        if (!clazz.isEnum()) {
            throw new RuntimeException("Class " + clazz.getName() + " is not an Enum");
        }

        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(annotationClass)).collect(Collectors.toList());
        if (fields.isEmpty()) {
            throw new RuntimeException("Enum " + clazz.getName() + " has no field annotated with " + annotationClass.getName());
        }
        if (fields.size() > 1) {
            throw new RuntimeException(formatMsg(fields.get(0), fields.get(1)));
        }

        Field field = fields.get(0);
        if (field == null) {
            throw new RuntimeException("Enum " + clazz.getName() + " has no field annotated with " + ANNOTATION_CLASS.getName());
        }
        field.setAccessible(true);
        return field;
    }


    public static String formatMsg(Field field, Field field2) {
        return String.format("Multiple 'as-value' properties defined ([field %s#%s] vs [field %s#%s])", field.getDeclaringClass().getName(), field.getName(), field2.getDeclaringClass().getName(), field2.getName());
    }

    public static String formatMsg(Type type, String name, Object value) {
        return String.format("【%s#%s:%s is not exist】", type.getTypeName(), name, value);
    }
}
