package com.davies.naraka.admin.service.exception;

/**
 * @author davies
 * @date 2022/1/28 1:44 PM
 */
public class RoleException extends SystemVerifyException{

    public RoleException() {
        super();
    }

    public RoleException(String message) {
        super(message);
    }

    public RoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoleException(Throwable cause) {
        super(cause);
    }

    protected RoleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
