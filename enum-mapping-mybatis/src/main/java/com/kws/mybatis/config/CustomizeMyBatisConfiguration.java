package com.kws.mybatis.config;

import com.kws.annotation.EnumValueMarkerFinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Objects;
import java.util.Set;

/**
 * @author kws
 * @date 2024-01-14 16:45
 */
@Slf4j
@Component
public class CustomizeMyBatisConfiguration implements ConfigurationCustomizer {
    /**
     * 可改成读取配置文件包路径.
     * 注意：
     * 如果需要从配置文件读取，直接通过@Value注解注入不会生效，
     * 需要实现EnvironmentAware接口，通过EnvironmentAware接口获取配置
     */
    private static final String BASE_SCAN_PACKAGE = "com.kws";
    public static final String ENUM_TYPE = "java.lang.Enum";

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void customize(Configuration configuration) {
        ClassPathScanningCandidateComponentProvider classPathScanning = new ClassPathScanningCandidateComponentProvider(false);
        classPathScanning.addIncludeFilter(new EnumTypeFilter());
        Set<BeanDefinition> enumsBeanDefinitions = classPathScanning.findCandidateComponents(BASE_SCAN_PACKAGE);
        if (CollectionUtils.isEmpty(enumsBeanDefinitions)) {
            return;
        }

        for (BeanDefinition bd : enumsBeanDefinitions) {
            try {
                log.info("====== register TypeHandler for Enum ======【{}】", bd.getBeanClassName());
                Class clazz = ClassUtils.forName(Objects.requireNonNull(bd.getBeanClassName()), getClass().getClassLoader());
                configuration.getTypeHandlerRegistry().register(clazz, new MyBatisEnumTypeHandler<>(clazz));
            } catch (Exception e) {
                log.error("====== Register Mybatis TypeHandler Failed. Enum:【{}】", bd.getBeanClassName(), e);
            }
        }
    }

    /**
     * 自定义枚举类型过滤器 <p>
     * 1.过滤枚举类型 <p>
     * 2.枚举类型字段必须打了枚举类型注解（或自定义注解） <p>
     *
     * @author kws
     * @date 2024-01-14 17:19
     */
    public static class EnumTypeFilter implements TypeFilter {
        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            String typeName = metadataReader.getClassMetadata().getSuperClassName();
            if (!ENUM_TYPE.equals(typeName)) {
                return false;
            }
            try {
                Class<?> clazz = ClassUtils.forName(metadataReader.getClassMetadata().getClassName(), getClass().getClassLoader());
                return EnumValueMarkerFinder.hasAnnotation(clazz);
            } catch (ClassNotFoundException e) {
                log.error("EnumTypeFilter match failed. Class not found: " + metadataReader.getClassMetadata(), e);
            }
            return false;
        }
    }
}
