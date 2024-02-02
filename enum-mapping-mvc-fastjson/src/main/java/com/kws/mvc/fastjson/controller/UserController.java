package com.kws.mvc.fastjson.controller;

import com.kws.common.entity.User;
import com.kws.common.enums.GenderEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author kws
 * @date 2024-02-03 14:56
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {


    /**
     * /user/get/obj?name=Bob&gender=3
     *
     * @param user 用户
     * @return {@link User}
     */
    @GetMapping("/get/obj")
    public User get(User user) {
        log.info("/get/obj user:{}", user);
        return user;
    }

    /**
     * /user/get/param?gender=1
     *
     * @param gender 性别
     * @return {@link GenderEnum}
     */
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


    /**
     * 请求:
     * application/json
     * {"name":"Bob","gender":2}
     *
     * @param user
     */
    @PostMapping("/save")
    public void save(@RequestBody User user) {
        // User(id=null, name=Bob, gender=GenderEnum{code=2, name='female', desc='女'})
        log.info("save user:{}", user);
    }



    @PostMapping("/list")
    public List<User> list() {
        Random random = new Random();
        GenderEnum[] genderEnums = GenderEnum.values();
        List<User> users = new ArrayList<>();
        users.add(new User("name#" + random.nextInt(), genderEnums[random.nextInt(genderEnums.length)]));
        users.forEach(u -> log.info("list user:{}", u));
        return users;
    }
}
