package com.kws.mybatis.mapper;

import com.kws.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author kws
 * @date 2024-01-14 09:35
 */
@Mapper
public interface UserMapper {
    void insert(User user);

    List<User> findAll();
}
