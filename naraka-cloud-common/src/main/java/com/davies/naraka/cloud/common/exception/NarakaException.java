package com.davies.naraka.cloud.common.exception;

/**
 * @author davies
 * @date 2022/3/19 19:23
 */
public class NarakaException extends RuntimeException {


    public NarakaException() {
        super();
    }

    public NarakaException(String message) {
        super(message);
    }

    public NarakaException(String message, Throwable cause) {
        super(message, cause);
    }

    public NarakaException(Throwable cause) {
        super(cause);
    }

    protected NarakaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
