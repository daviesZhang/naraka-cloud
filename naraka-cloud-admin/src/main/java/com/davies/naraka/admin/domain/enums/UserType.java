package com.davies.naraka.admin.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author davies
 * @date 2022/1/24 1:13 PM
 */
public enum UserType {

    /**
     * 0 管理员 1客户
     */
    ADMIN(0),
    CUSTOMER(1);




    /**
     * 状态
     */
    @EnumValue
    @JsonValue
    private final int code;

    UserType(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
