package com.davies.naraka.puppeteer.domain.enums;

/**
 * @author davies
 */

public enum StepAction {

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

    public int getCode() {
        return code;
    }
}
