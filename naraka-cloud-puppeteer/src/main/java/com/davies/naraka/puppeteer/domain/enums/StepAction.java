package com.davies.naraka.puppeteer.domain.enums;

import java.util.function.Supplier;

/**
 * @author davies
 */

public enum StepAction implements Supplier<Integer> {

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
    public Integer get() {
        return code;
    }
}
