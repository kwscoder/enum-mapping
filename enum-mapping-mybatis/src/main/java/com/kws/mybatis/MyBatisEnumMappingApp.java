package com.kws.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author kws
 * @date 2024/1/15 12:44
 */

@SpringBootApplication
@MapperScan(basePackages = "com.kws.mybatis.mapper")
public class MyBatisEnumMappingApp {
    public static void main(String[] args) {
        SpringApplication.run(MyBatisEnumMappingApp.class, args);
    }
}