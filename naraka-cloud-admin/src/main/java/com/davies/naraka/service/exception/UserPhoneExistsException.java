package com.davies.naraka.service.exception;

/**
 * @author davies
 * @date 2022/1/24 12:58 PM
 */
public class UserPhoneExistsException extends UserException{

    public UserPhoneExistsException() {
        super();
    }

    public UserPhoneExistsException(String message) {
        super(message);
    }

    public UserPhoneExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserPhoneExistsException(Throwable cause) {
        super(cause);
    }
}
