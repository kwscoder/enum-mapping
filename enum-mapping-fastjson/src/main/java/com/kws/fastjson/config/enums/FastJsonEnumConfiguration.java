package com.kws.fastjson.config.enums;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author kws
 * @date 2024-01-18 22:15
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class FastJsonEnumConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ObjectSerializer.class, ObjectDeserializer.class})
    public static class CustomizeEnumGlobalConfig {
        @PostConstruct
        public void init() {
            if (log.isDebugEnabled()) {
                log.debug("====== Register global customized enum serializer and deserializer ======");
            }
            SerializeConfig serializeConfig = SerializeConfig.getGlobalInstance();
            ParserConfig parserConfig = ParserConfig.getGlobalInstance();

            CustomizeEnumModule customizeEnumModule = new CustomizeEnumModule();
            serializeConfig.register(customizeEnumModule);
            parserConfig.register(customizeEnumModule);
        }
    }

}
