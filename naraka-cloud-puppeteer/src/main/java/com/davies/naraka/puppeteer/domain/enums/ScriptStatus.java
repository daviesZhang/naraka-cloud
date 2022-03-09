package com.davies.naraka.puppeteer.domain.enums;

import com.davies.naraka.autoconfigure.jpa.EnumCodePersistence;

/**
 * @author davies
 */
public enum ScriptStatus implements EnumCodePersistence {

    ENABLE(10),
    DISABLE(11);

    private final int code;

    ScriptStatus(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
