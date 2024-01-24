package com.kws.fastjson.config;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.kws.annotation.EnumValueMarkerFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author kongweishen
 * @date 2023-12-29 18:33
 */
@Slf4j
public class FastJsonEnumDeserializer implements ObjectDeserializer {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        try {
            Field field = EnumValueMarkerFinder.find((Class<T>) type);

            Object value = parser.parse();
            String name = field.getName();
            log.info("==> deserialize {}#{}【{}】", type.getTypeName(), name, value);

            PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor((Class<?>) type, name);
            Method readMethod = Optional.ofNullable(propertyDescriptor).map(PropertyDescriptor::getReadMethod).orElseThrow(() -> new RuntimeException("获取属性" + name + "失败，缺少get方法"));
            for (T enumConstant : ((Class<T>) type).getEnumConstants()) {
                Object codeValue = readMethod.invoke(enumConstant);
                if (value.equals(codeValue)) {
                    return enumConstant;
                }
            }
        } catch (Exception e) {
            throw new JSONException("getEnumValue error", e);
        }
        return null;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
