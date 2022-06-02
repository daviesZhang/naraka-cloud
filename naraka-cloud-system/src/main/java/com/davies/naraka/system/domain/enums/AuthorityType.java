package com.davies.naraka.system.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.function.Supplier;

/**
 * @author davies
 * @date 2022/6/1 16:00
 */
public enum AuthorityType implements Supplier<Integer> {


    API(0);
    @JsonValue
    private final int code;

    AuthorityType(int code) {
        this.code = code;
    }


    @Override
    public Integer get() {
        return this.code;
    }
}
