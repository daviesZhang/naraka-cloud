package com.davies.naraka.admin.domain.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author davies
 * @date 2022/1/24 1:13 PM
 */
public enum AuthorityProcessorType {


    /**
     * 过滤字段
     */
    FILTER(0),
    /**
     * 脱敏
     */
    DESENSITIZATION(1),
    /**
     * 数据筛选,row级别
     */
    SKIP(2);


    /**
     * 状态
     */
    @EnumValue
    @JsonValue
    private final int code;

    AuthorityProcessorType(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
