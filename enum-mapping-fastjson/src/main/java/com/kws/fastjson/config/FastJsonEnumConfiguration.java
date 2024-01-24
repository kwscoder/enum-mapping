package com.kws.fastjson.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.spi.Module;
import com.kws.annotation.EnumValueMarker;
import com.kws.annotation.EnumValueMarkerFinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author kongweishen
 * @date 2024-01-04 22:15
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ObjectSerializer.class, ObjectDeserializer.class})
//@ConditionalOnMissingClass({"com.alibaba.fastjson.serializer.ObjectSerializer", "com.alibaba.fastjson.parser.deserializer.ObjectDeserializer"})
public class FastJsonEnumConfiguration {
    public FastJsonEnumConfiguration() {
        log.info("****** constructor ******");
    }

    @PostConstruct
    public void init() {
        log.info("****** init ******");
        log.debug("====== Register fastjson custom enum serialization and deserialization ======");
        log.info("====== 加载Fastjson枚举类自定义序列化、反序列化 ======");
        MyEnumModule customizeEnumModule = new MyEnumModule();
        SerializeConfig instance = SerializeConfig.getGlobalInstance();
        instance.register(customizeEnumModule);

        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
        parserConfig.register(customizeEnumModule);
    }

    /*
     * 当在class上配置 @ConditionalOnMissingClass 注解时，若指定类存在，则不会执行static代码块代码
     */
    static {
        log.info("****** static block ******");
//        log.info("====== 加载Fastjson枚举类自定义序列化、反序列化 ======");
//        MyEnumModule customizeEnumModule = new MyEnumModule();
//        SerializeConfig instance = SerializeConfig.getGlobalInstance();
//        instance.register(customizeEnumModule);
//
//        ParserConfig parserConfig = ParserConfig.getGlobalInstance();
//        parserConfig.register(customizeEnumModule);
    }

    @Slf4j
    public static class MyEnumModule implements Module {
        public MyEnumModule() {
        }

        @Override
        public ObjectDeserializer createDeserializer(ParserConfig config, Class type) {
            if (!type.isEnum() || !EnumValueMarkerFinder.hasAnnotation(type, EnumValueMarker.class)) {
                return null;
            }
            return new FastJsonEnumDeserializer();
        }

        @Override
        public ObjectSerializer createSerializer(SerializeConfig config, Class type) {
            if (!type.isEnum() || !EnumValueMarkerFinder.hasAnnotation(type, EnumValueMarker.class)) {
                return null;
            }
            return new FastJsonEnumSerializer();
        }
    }

}
