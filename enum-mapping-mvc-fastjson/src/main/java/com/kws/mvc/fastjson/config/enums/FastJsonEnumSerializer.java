package com.kws.mvc.fastjson.config.enums;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.kws.annotation.EnumValueMarkerFinder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author kws
 * @date 2024-01-18 21:03
 */
@Slf4j
public class FastJsonEnumSerializer implements ObjectSerializer {
    public FastJsonEnumSerializer() {
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if (object == null) {
            return;
        }
        try {
            Field field = EnumValueMarkerFinder.find(object.getClass());
            Object val = field.get(object);
            log.info("==> serialize {}#{}【{}】", fieldType != null ? fieldType.getTypeName() : object.getClass().getName(), field.getName(), val);
            serializer.write(val);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
