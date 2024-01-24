package com.kws.mybatis.mapper;

import com.kws.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author kongweishen
 * @date 2024-01-24 09:35
 */
@Mapper
public interface UserMapper {
    void insert(User user);

    List<User> findAll();
}
