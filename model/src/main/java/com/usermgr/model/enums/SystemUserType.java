package com.usermgr.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SystemUserType implements BaseEnum {

    COMMON(0, "普通用户"),
    ADMIN(1, "管理员");

    @EnumValue
    @JsonValue
    private Integer code;

    private String name;


    SystemUserType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
