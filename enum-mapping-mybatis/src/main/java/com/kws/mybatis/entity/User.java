package com.kws.mybatis.entity;

import com.kws.mybatis.enums.GenderEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author kongweishen
 * @date 2024-01-24 09:36
 */
@Getter
@Setter
public class User {
    private Integer id;
    private String name;
    private GenderEnum gender;
}
