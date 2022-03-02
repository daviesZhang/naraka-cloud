package com.davies.naraka.autoconfigure.security;

/**
 * 用户名缺失,调用方没有传递或者当前CurrentUserNameSupplier获取不到
 *
 * @author davies
 * @date 2022/3/2 8:48 PM
 */
public class UserNameMissingException extends RuntimeException {

    public UserNameMissingException() {
        super("call CurrentUserNameSupplier#get() return null~");
    }

    public UserNameMissingException(String message) {
        super(message);
    }

    public UserNameMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNameMissingException(Throwable cause) {
        super(cause);
    }

    protected UserNameMissingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
