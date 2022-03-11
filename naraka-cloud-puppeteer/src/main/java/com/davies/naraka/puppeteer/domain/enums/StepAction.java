package com.davies.naraka.puppeteer.domain.enums;

import com.davies.naraka.autoconfigure.EnumCodePersistence;

/**
 * @author davies
 */

public enum StepAction implements EnumCodePersistence {

    /**
     *
     */
    OPEN(0),
    /**
     *
     */
    CLOSE(1);

    private final int code;

    StepAction(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
