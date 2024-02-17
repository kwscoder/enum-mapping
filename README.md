# enum-mapping
自定义通用枚举类型fastjson序列化、反序列化 + mybatis枚举类型解析器（TypeHandler）

## enum-mapping-annotations
自定义枚举类型注解  
当前使用的是Jackson自带的注解`@JsonValue`，也可以使用自定义的`@EnumValueMarker`注解

## enum-mapping-common
公用的entity对象及枚举类型对象

## enum-mapping-fastjson
自定义非mvc场景下fastjson枚举类型通用序列化反序列化

## enum-mapping-mvc-fastjson
自定义mvc中使用fastjson作为MessageConvert的通用枚举类型序列化与反序列化，[点击查看详情](./enum-mapping-mvc-fastjson/README.md)

## enum-mapping-mvc-jackson
自定义mvc中使用jackson作为MessageConvert的通用枚举类型序列化与反序列化

## enum-mapping-mybatis
自定义MyBatis通用枚举类型处理器，[点击查看详情](./enum-mapping-mybatis/README.md)
