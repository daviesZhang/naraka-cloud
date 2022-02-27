package com.davies.naraka.service.exception;

/**
 * @author davies
 * @date 2022/1/24 12:58 PM
 */
public class UserEmailExistsException extends UserException{

    public UserEmailExistsException() {
        super();
    }

    public UserEmailExistsException(String message) {
        super(message);
    }

    public UserEmailExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserEmailExistsException(Throwable cause) {
        super(cause);
    }
}
