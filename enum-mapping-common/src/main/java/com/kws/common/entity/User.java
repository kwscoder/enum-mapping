package com.kws.common.entity;

import com.kws.common.enums.GenderEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author kws
 * @date 2024-01-24 21:36
 */
@Getter
@Setter
@ToString
public class User {
    private Integer id;
    private String name;
    private GenderEnum gender;

    public User() {
    }

    public User(String name, GenderEnum gender) {
        this.name = name;
        this.gender = gender;
    }

}
