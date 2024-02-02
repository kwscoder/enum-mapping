package com.kws.mybatis.controller;

import com.kws.common.entity.User;
import com.kws.mybatis.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author kws
 * @date 2024-01-14 13:56
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    @PostMapping("/save")
    public void save(@RequestBody User user) {
        userMapper.insert(user);
    }

    @GetMapping("/list")
    public List<User> list() {
        return userMapper.findAll();
    }
}
