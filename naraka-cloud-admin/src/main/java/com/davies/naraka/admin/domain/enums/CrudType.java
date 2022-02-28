package com.davies.naraka.admin.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author davies
 * @date 2022/2/21 4:51 PM
 */
public enum CrudType {

    /**
     *
     */
    CREATE(0),
    UPDATE(1),
    QUERY(2),
    DELETE(3);


    /**
     * 状态
     */
    @EnumValue
    @JsonValue
    private final int code;

    CrudType(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
