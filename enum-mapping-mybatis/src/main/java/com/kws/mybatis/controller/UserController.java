package com.kws.mybatis.controller;

import com.kws.mybatis.entity.User;
import com.kws.mybatis.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author kongweishen
 * @date 2024-01-24 13:56
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
