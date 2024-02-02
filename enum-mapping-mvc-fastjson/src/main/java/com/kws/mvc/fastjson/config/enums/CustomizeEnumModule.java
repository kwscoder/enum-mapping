package com.kws.mvc.fastjson.config.enums;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.spi.Module;
import com.kws.annotation.EnumValueMarkerFinder;

/**
 * @author kws
 * @date 2024-01-18 21:12
 */
public class CustomizeEnumModule implements Module {
    public CustomizeEnumModule() {
    }

    @Override
    public ObjectDeserializer createDeserializer(ParserConfig config, Class type) {
        return isCustomizedEnum(type) ? new FastJsonEnumDeserializer() : null;
    }

    @Override
    public ObjectSerializer createSerializer(SerializeConfig config, Class type) {
        return isCustomizedEnum(type) ? new FastJsonEnumSerializer() : null;
    }

    private boolean isCustomizedEnum(Class<?> type) {
        return type.isEnum() && EnumValueMarkerFinder.hasAnnotation(type);
    }

}
