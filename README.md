# enum-mapping
自定义通用枚举类型fastjson序列化、反序列化 + mybatis枚举类型解析器（TypeHandler）

## enum-mapping-annotations
自定义枚举类型注解
当前使用的是Jackson自带的注解`@JsonValue`，也可以使用自定义的`@EnumValueMarker`注解

## enum-mapping-fastjson
自定义fastjson枚举类型通用序列化反序列化

## enum-mapping-mybatis
### MyBatis自定义通用枚举类解析器（自动注册通用枚举类解析器）
1. 自定义Configuration配置类：`CustomizeMyBatisConfiguration.java`
2. 实现 `org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer` 接口
3. 实现 `ConfigurationCustomizer` 接口中的 customize 方法
4. 扫描classpath下指定包下的所有枚举类，并过滤出字段打了`@JsonValue`注解的枚举类
5. 通过`configuration`对象获取到`TypeHandlerRegistry`，使用`TypeHandlerRegistry`将不同枚举类自动注册到通用类型的枚举解析器中
6. 接下来自定义通用枚举解析器：`MyBatisEnumTypeHandler` 继承自 `BaseTypeHandler`
7. 在自定义的枚举类型解析器`MyBatisEnumTypeHandler`中实现枚举值解析