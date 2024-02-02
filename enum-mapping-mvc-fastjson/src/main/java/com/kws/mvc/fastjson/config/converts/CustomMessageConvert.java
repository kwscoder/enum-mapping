package com.kws.mvc.fastjson.config.converts;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * @author kws
 * @date 2024-02-03 15:58
 */
@Configuration(proxyBeanMethods = false)
public class CustomMessageConvert {

    @Bean
    public HttpMessageConverter<?> fastJsonHttpMessageConverter() {
        return new FastJsonHttpMessageConverter();
    }
}
