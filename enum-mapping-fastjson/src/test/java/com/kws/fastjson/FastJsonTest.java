package com.kws.fastjson;

import com.alibaba.fastjson.JSON;
import com.kws.common.entity.User;
import com.kws.common.enums.GenderEnum;
import com.kws.fastjson.config.enums.FastJsonEnumConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author kws
 * @date 2024-01-21 12:52
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class FastJsonTest {

    private User user;

    @Before
    public void before() {
        user = new User();
        user.setId(1);
        user.setName("User-1");
        user.setGender(GenderEnum.FEMALE);
    }

    /**
     * 注释掉配置类{@link FastJsonEnumConfiguration}中这行代码 => // @Configuration(proxyBeanMethods = false)
     * 未启用自定义fastjson枚举类型序列化反序列化，序列化结果如下:
     */
    @Test
    public void testSerialize() {
        System.out.println(JSON.toJSONString(user));    // {"gender":"FEMALE","id":1,"name":"User-1"}
    }

    /**
     * 打开配置类{@link FastJsonEnumConfiguration}中这行代码 => @Configuration(proxyBeanMethods = false)
     * 启用自定义fastjson枚举类型序列化反序列化后，序列化结果如下:
     */
    @Test
    public void testCustomizedSerialize() {
        System.out.println(JSON.toJSONString(user));    // {"gender":2,"id":1,"name":"User-1"}
    }


    @Test
    public void testDeserialize() {

    }
}
