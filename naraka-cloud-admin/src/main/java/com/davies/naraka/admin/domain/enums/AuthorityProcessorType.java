package com.davies.naraka.admin.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author davies
 * @date 2022/1/24 1:13 PM
 */
public enum AuthorityProcessorType {


    /**
     * 过滤
     */
    FILTER(0),
    /**
     * 脱敏
     */
    DESENSITIZATION(1);


    /**
     * 状态
     */
    @JsonValue
    @EnumValue
    private final int code;

    AuthorityProcessorType(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
