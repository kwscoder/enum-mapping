# 自定义 Fastjson 通用枚举类型序列化与反序列化
在使用SpringMVC前端发送请求到后端的过程中几乎都会有枚举类型的参数需要传递，就用性别来看平时的使用过程（gender 1:男 2:女），然后再讨论怎么针对fastjson进行自定义通用枚举序列化与反序列的处理
注意：这里`MessageConvert`使用的是 **`FastJsonHttpMessageConverter`**

## 公共entity定义：
   User类定义：
   ```java
   @Getter
   @Setter
   @ToString
   public class User {
      private Integer id;
      private String name;
      private Integer gender;
   }
   ```
   性别枚举类定义：
   ```java
   @Getter
   @RequiredArgsConstructor
   public enum GenderEnum {
        MALE(1, "male", "男"),
        FEMALE(2, "female", "女");
      
        @JsonValue  // jackson 包中的注解
        private final Integer code;
        private final String name;
        private final String desc;
      
        @Override
        public String toString() {
            return "GenderEnum{" + "code=" + code + ", name='" + name + '\'' + ", desc='" + desc + '\'' + '}';
        }
   }
   ```

一般前端发送请求到后端的方式有多种：`POST`、`GET`等，下面以`POST`和`GET`方式举例
### `POST`方式
   UserController：
   ```java
   @Slf4j
   @RestController
   @RequestMapping("/user")
   public class UserController {
        /**
         * application/json ==> {"name":"Bob","gender":2}
         */
        @PostMapping("/save")
        public void save(@RequestBody User user) {
            // 此处user对象中的gender是Integer类型
            log.info("save user:{}", user);
        }
   }
   ```

### `GET`方式:
   UserController:
   ```java
    @Slf4j
    @RestController
    @RequestMapping("/user")
    public class UserController {
        // GET /user/get/obj?name=Bob&gender=2
        @GetMapping("/get/obj")
        public User get(User user) {
            log.info("/get/obj user:{}", user);
            return user;
        }
    
        // GET /user/get/param?gender=1
        @GetMapping("/get/param")
        public Integer get(@RequestParam Integer gender) {
            log.info("/get/param gender:{}", gender);
            return gender;
        }    

        // GET /user/get/1
        @GetMapping("/get/{gender}")
        public Integer path(@PathVariable Integer gender) {
            log.info("/get/param gender:{}", gender);
            return gender;
        }
    }
   ```

> 从上述示例代码中可以看出，后端在接收`性别`参数时都是使用的Integer类型接收，然后再根据`code`值手动转换成对应的枚举类型，而通常情况下更期望是直接获取到转换后的枚举类型，
此时该如何实现自动根据枚举类型code值反序列化成对应的枚举对象？

## 自定义通用枚举类型序列化与反序列化器
实现思路：
1. 自定义注解用于标识枚举字段`code`值（可以使用Jackson自带的`@JsonValue`注解，也可以单独自定义注解），注解标识的字段类型非固定类型，可为`Integer`、`Long`、`String`等其他基本类型或其他类型（其他类型请多测试）
2. 实现自定义序列化类：[FastJsonEnumSerializer.java](src%2Fmain%2Fjava%2Fcom%2Fkws%2Fmvc%2Ffastjson%2Fconfig%2Fenums%2FFastJsonEnumSerializer.java)
   ```java
   @Slf4j
   public class FastJsonEnumSerializer implements ObjectSerializer {
       public FastJsonEnumSerializer() {}
       @Override
       public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
           if (object == null) {
               return;
           }
           try {
               // 获取枚举类字段加了@JsonValue注解或自定义注解的字段，因为需要序列化的就是当前属性的值
               Field field = EnumValueMarkerFinder.find(object.getClass());
               Object val = field.get(object);
               log.info("==> serialize {}#{}【{}】", fieldType != null ? fieldType.getTypeName() : object.getClass().getName(), field.getName(), val);
               serializer.write(val);
           } catch (IllegalAccessException e) {
               throw new RuntimeException(e);
           }
       }
   }
   ```
3. 实现自定义反序列化类：[FastJsonEnumDeserializer.java](src%2Fmain%2Fjava%2Fcom%2Fkws%2Fmvc%2Ffastjson%2Fconfig%2Fenums%2FFastJsonEnumDeserializer.java)
   ```java
   @Slf4j
   public class FastJsonEnumDeserializer implements ObjectDeserializer {
   
       @SuppressWarnings("unchecked")
       @Override
       public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
           try {
               Field field = EnumValueMarkerFinder.find((Class<T>) type);
   
               Object value = parser.parse();
               String name = field.getName();
               log.info("==> deserialize {}#{}【{}】", type.getTypeName(), name, value);

               // 根据code值反序列化成对应的枚举类
               PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor((Class<?>) type, name);
               Method readMethod = Optional.ofNullable(propertyDescriptor).map(PropertyDescriptor::getReadMethod).orElseThrow(() -> new RuntimeException("获取属性" + name + "失败，缺少get方法"));
               for (T enumConstant : ((Class<T>) type).getEnumConstants()) {
                   Object codeValue = readMethod.invoke(enumConstant);
                   if (value.equals(codeValue)) {
                       return enumConstant;
                   }
               }
               log.warn("==> deserialized failed. {}", EnumValueMarkerFinder.formatMsg(type, name, value));
           } catch (Exception e) {
               log.error("==> deserialized failed.", e);
               throw new JSONException("getEnumValue error", e);
           }
           return null;
       }
   
       @Override
       public int getFastMatchToken() {
           return 0;
       }
   }
   ```
4. 上述步骤中已经完成了fastjson枚举类型序列化与反序列化的自定义功能，那么该如何生效呢？
5. 接下来我们通过自定义一个Module：[CustomizeEnumModule.java](src%2Fmain%2Fjava%2Fcom%2Fkws%2Fmvc%2Ffastjson%2Fconfig%2Fenums%2FCustomizeEnumModule.java)并实现`com.alibaba.fastjson.spi.Module`接口中的`createDeserializer`和`createSerializer`方法来将刚才定义的反序列化与序列化功能接入fastjson
   ```java
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
   
       // 是否为枚举类型且当前枚举类型属性中有加了@JsonValue注解或自定义注解的属性
       private boolean isCustomizedEnum(Class<?> type) {
           return type.isEnum() && EnumValueMarkerFinder.hasAnnotation(type);
       }
   }
   ```
6. Module已经定义完成，接下来就是如何将自定义的Module应用到fastjson的序列化与反序列化中了？
7. 自定义FastJsonEnumConfiguration配置类，用来注册自定义的Module，及将自定义的序列化与反序列化器注册进去
   ```java
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
   
               // 在这里
               CustomizeEnumModule customizeEnumModule = new CustomizeEnumModule();
               serializeConfig.register(customizeEnumModule);
               parserConfig.register(customizeEnumModule);
           }
       }
   }
   ```
8. 完成上述步骤后调整User对象的定义，可以使用`GenderEnum`枚举类来接收性别参数而不需要再使用Integer类型接收参数了
   User定义如下：
    ```java
    @Getter
    @Setter
    @ToString
    public class User {
        private Integer id;
        private String name;
        private GenderEnum gender;
    
        public User() {}
    
        public User(String name, GenderEnum gender) {
            this.name = name;
            this.gender = gender;
        }
    }
    ```
9. UserController.java
    ```java
    @Slf4j
    @RestController
    @RequestMapping("/user")
    public class UserController {
        // application/json ===> {"name":"Bob","gender":2}
        @PostMapping("/save")
        public void save(@RequestBody User user) {
            // 现在user对象中的性别属性已经是枚举类型数据了
            log.info("save user:{}", user);
        }
    }
    ```
10. 目前针对`POST`方式`application/json`的请求已经都可以自动将参数中的枚举类型进行自动完成序列化与反序列化了，
   但是在同事的提醒下，发现`GET`方法的请求参数还不行，再继续完成对`GET`方式json枚举类型参数的序列化与反序列化

11. 自定义[CustomConverterFactory.java](src%2Fmain%2Fjava%2Fcom%2Fkws%2Fmvc%2Ffastjson%2Fconfig%2Fconverts%2FCustomConverterFactory.java)并实现`org.springframework.core.convert.converter.ConditionalConverter`接口和`org.springframework.core.convert.converter.ConverterFactory`接口
    ```java
    @Slf4j
    public class CustomConverterFactory implements ConditionalConverter, ConverterFactory<String, Enum<?>> {
    
        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return EnumValueMarkerFinder.hasAnnotation(targetType.getType());
        }
    
        @Override
        public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
            if (!targetType.isEnum()) {
                return null;
            }
            try {
                Field field = EnumValueMarkerFinder.find(targetType);
                return new CustomEnumConvert<>(targetType, field);
            } catch (Exception e) {
                log.error("字段属性转换失败", e);
            }
            return null;
        }
    
        @RequiredArgsConstructor
        private static class CustomEnumConvert<T extends Enum<?>> implements Converter<String, T>{
            private final Class<T> enumType;
            private final Field field;
    
            @Override
            public T convert(String source) {
                try {
                    PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(enumType, field.getName());
                    Method readMethod = Optional.ofNullable(propertyDescriptor).map(PropertyDescriptor::getReadMethod).orElseThrow(() -> new RuntimeException("获取属性" + field.getName() + "失败，缺少get方法"));
                    for (T enumConstant : enumType.getEnumConstants()) {
                        Object codeValue = readMethod.invoke(enumConstant);
                        if (codeValue != null && source.equals(codeValue.toString())) {
                            return enumConstant;
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        }
    }
    ```
12. UserController.java中get请求示例代码：
    ```java
        @Slf4j
        @RestController
        @RequestMapping("/user")
        public class UserController {
        
            //GET /user/get/obj?name=Bob&gender=3
            @GetMapping("/get/obj")
            public User get(User user) {
                log.info("/get/obj user:{}", user);
                return user;
            }
        
            // GET /user/get/param?gender=1
            @GetMapping("/get/param")
            public GenderEnum get(@RequestParam GenderEnum gender) {
                log.info("/get/param GenderEnum:{}", gender);
                return gender;
            }
        
            @GetMapping("/get/{gender}")
            public GenderEnum path(@PathVariable GenderEnum gender) {
                log.info("/get/param GenderEnum:{}", gender);
                return gender;
            }
        }
    ```
13. 完整代码已发布github
    [github: enum-mapping](https://github.com/kwscoder/enum-mapping)
    模块：`enum-mapping-mvc-fastjson`