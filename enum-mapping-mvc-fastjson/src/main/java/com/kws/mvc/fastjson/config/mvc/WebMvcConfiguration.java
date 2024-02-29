package com.kws.mvc.fastjson.config.mvc;

import com.kws.mvc.fastjson.config.converts.CustomConverterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author kws
 * @date 2024-02-01 21:31
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new CustomConverterFactory());
    }
}
