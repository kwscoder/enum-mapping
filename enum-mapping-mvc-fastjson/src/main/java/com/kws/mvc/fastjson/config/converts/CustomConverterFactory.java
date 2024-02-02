package com.kws.mvc.fastjson.config.converts;

import com.kws.annotation.EnumValueMarkerFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author kws
 * @date 2024-02-03 17:40
 */
@Slf4j
public class CustomConverterFactory implements ConditionalConverter, ConverterFactory<String, Enum<?>> {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return EnumValueMarkerFinder.hasAnnotation(targetType.getType());
    }

    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        if (!targetType.isEnum()) {
            return null;
        }
        try {
            Field field = EnumValueMarkerFinder.find(targetType);
            return new CustomEnumConvert<>(targetType, field);
        } catch (Exception e) {
            log.error("字段属性转换失败", e);
        }
        return null;
    }

    @RequiredArgsConstructor
    private static class CustomEnumConvert<T extends Enum<?>> implements Converter<String, T>{
        private final Class<T> enumType;
        private final Field field;

        @Override
        public T convert(String source) {
            try {
                PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(enumType, field.getName());
                Method readMethod = Optional.ofNullable(propertyDescriptor).map(PropertyDescriptor::getReadMethod).orElseThrow(() -> new RuntimeException("获取属性" + field.getName() + "失败，缺少get方法"));
                for (T enumConstant : enumType.getEnumConstants()) {
                    Object codeValue = readMethod.invoke(enumConstant);
                    if (codeValue != null && source.equals(codeValue.toString())) {
                        return enumConstant;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }


}
