package com.davies.naraka.admin.service.exception;

/**
 * @author davies
 * @date 2022/1/24 12:58 PM
 */
public class UserNotFoundException extends UserException {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
