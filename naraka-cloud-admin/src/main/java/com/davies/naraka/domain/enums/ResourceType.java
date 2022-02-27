package com.davies.naraka.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author davies
 * @date 2022/1/25 6:35 PM
 */
public enum ResourceType {
    /**
     * 0 URL 1菜单
     */
    URL(0),
    MENU(1);



    /**
     * 状态
     */
    @EnumValue
    @JsonValue
    private final int code;

    ResourceType(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
