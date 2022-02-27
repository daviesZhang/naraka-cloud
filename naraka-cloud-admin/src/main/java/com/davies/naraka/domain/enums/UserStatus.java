package com.davies.naraka.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author davies
 * @date 2022/1/24 1:13 PM
 */
public enum UserStatus {

    /**
     * 0 启用 1禁用
     */
    ENABLE(0),
    DISABLE(1);



    /**
     * 状态
     */
    @EnumValue
    @JsonValue
    public final int code;

    UserStatus(int code) {
        this.code = code;
    }



    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
