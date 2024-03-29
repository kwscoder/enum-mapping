# 自定义 MyBatis 通用枚举类型解析器
在使用`MyBatis`的过程中，我们经常会使用到枚举类型的数据，
一般在保存数据时只是想将枚举类型的code值存入到数据库中，查询时希望能自动根据code值映射出对应的枚举对象出现，而不是查询出code值然后再手动根据code值找到对应的枚举对象的转换

## 官方注册方案
官方方案：<https://mybatis.org/mybatis-3/zh_CN/configuration.html#typeHandlers>  
**无法对所有枚举类型进行通用注册（有可能是没找到正确的方式，如果有，恳请大家指导）**


## 自动注册方案
实现思路如下：
1. 自定义注解用于标识枚举字段`code`值（可以使用Jackson自带的`@JsonValue`注解，也可以单独自定义注解），注解标识的字段类型非固定类型，可为`Integer`、`Long`、`String`等其他基本类型或其他类型（其他类型请多测试）
2. 自定义枚举类型处理器 [MyBatisEnumTypeHandler.java](src%2Fmain%2Fjava%2Fcom%2Fkws%2Fmybatis%2Fconfig%2FMyBatisEnumTypeHandler.java) 继承自`org.apache.ibatis.type.BaseTypeHandler`，用于处理枚举类型数据的保存和查询使用
   ```java
   @Slf4j
   public class MyBatisEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
   
       private final Class<E> type;
       public MyBatisEnumTypeHandler(Class<E> type) {
           this.type = type;
       }
   
       @Override
       public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
           try {
               Field field = EnumValueMarkerFinder.find(type);
               Object val = field.get(parameter);
               if (jdbcType == null) {
                   ps.setObject(i, val);
               } else {
                   ps.setObject(i, val, jdbcType.TYPE_CODE);
               }
           } catch (IllegalAccessException e) {
               throw new RuntimeException(e);
           }
       }
   
       @Override
       public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
           Object s = rs.getObject(columnName);
           return findTargetEnum(s, type);
       }
   
       @Override
       public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
           Object s = rs.getObject(columnIndex);
           return findTargetEnum(s, type);
       }
   
       @Override
       public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
           Object s = cs.getObject(columnIndex);
           return findTargetEnum(s, type);
       }
   
       private E findTargetEnum(Object val, Class<E> type) {
           if (val == null) {
               return null;
           }
           try {
               Field field = EnumValueMarkerFinder.find(type);
               for (E enumConstant : type.getEnumConstants()) {
                   Object o = field.get(enumConstant);
                   if (val.equals(o)) {
                       return enumConstant;
                   }
               }
           } catch (IllegalAccessException e) {
               log.error("Handle enum failed...", e);
           }
           return null;
       }
   }
   ```
3. **接下来，怎么将自定义的枚举类型处理器用于处理所有枚举类型的数据？**
4. 为了实现所有的枚举都自动注册通用类型转换器，这里需要自定义一个配置类 [CustomizeMyBatisConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fkws%2Fmybatis%2Fconfig%2FCustomizeMyBatisConfiguration.java) 并实现`org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer`接口 
   1. 实现该接口后，可以获取到`org.apache.ibatis.session.Configuration`配置类，
   2. 使用`Configuration`配置类获取到`TypeHandlerRegistry`注册器，
   3. 再使用`TypeHandlerRegistry`注册器将需要处理的枚举类类型解析器注册进去
   ```java
   public class CustomizeMyBatisConfiguration implements ConfigurationCustomizer{ 
       public void customize(Configuration configuration) {
           // 将自定义的通用枚举类型处理器`MyBatisEnumTypeHandler`注册进去
           // Class clazz = null; // 怎么获取到需要处理的枚举类，即字段中标了@JsonValue注解或自定义注解的枚举类? 
           configuration.getTypeHandlerRegistry().register(clazz, new MyBatisEnumTypeHandler<>(clazz));
       }
   }
   ```
5. 获取所有需要注册到通用枚举类型处理器中的枚举类
   1. 在 **`customize`** 方法中通过`Spring`框架中`ClassPathScanningCandidateComponentProvider`扫描器在`classpath`下扫描出指定包下的枚举类
   2. 自定义一个类型过滤器`com.kws.mybatis.config.CustomizeMyBatisConfiguration.EnumTypeFilter`，用于在类路径扫描时，过滤出需要处理的枚举类（1.枚举类型 2.枚举类型中含有自定义注解字段）
      ```java
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
      ```
   3. 过滤出需要处理的枚举类后，通过`TypeHandlerRegistry`将当前枚举类型使用通用的枚举类型处理器注册到类型处理器中
   4. 具体注册代码如下
      ```java
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
      ```
6. 完整代码已发布github：  
   [github: enum-mapping](https://github.com/kwscoder/enum-mapping)
   模块：`enum-mapping-mybatis`