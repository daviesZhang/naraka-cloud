package com.davies.naraka.puppeteer.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.function.Supplier;

/**
 * @author davies
 */
public enum ScriptStatus implements Supplier<Integer> {

    ENABLE(10),
    DISABLE(11);

    @JsonValue
    private final int code;

    ScriptStatus(int code) {
        this.code = code;
    }

    @Override
    public Integer get() {
        return code;
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }
}
