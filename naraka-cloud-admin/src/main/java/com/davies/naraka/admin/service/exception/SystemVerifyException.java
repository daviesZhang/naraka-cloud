package com.davies.naraka.admin.service.exception;

import com.davies.naraka.cloud.common.exception.NarakaException;

/**
 * @author davies
 * @date 2022/1/28 2:09 PM
 */
public class SystemVerifyException extends NarakaException {


    public SystemVerifyException() {
        super();
    }

    public SystemVerifyException(String message) {
        super(message);
    }

    public SystemVerifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemVerifyException(Throwable cause) {
        super(cause);
    }

    protected SystemVerifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
