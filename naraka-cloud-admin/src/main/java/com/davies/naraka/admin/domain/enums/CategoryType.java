package com.davies.naraka.admin.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author davies
 * @date 2022/2/8 12:54 PM
 */
public enum CategoryType {
    /**
     * 0 用户组 1菜单
     */
    GROUP(0),
    MENU(1);



    /**
     * 状态
     */
    @EnumValue
    @JsonValue
    private final int code;

    CategoryType(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
