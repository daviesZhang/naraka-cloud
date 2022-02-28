package com.davies.naraka.admin.service.exception;

/**
 * @author davies
 * @date 2022/1/24 12:58 PM
 */
public class UserNameExistsException extends UserException{

    public UserNameExistsException() {
    }

    public UserNameExistsException(String message) {
        super(message);
    }

    public UserNameExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNameExistsException(Throwable cause) {
        super(cause);
    }
}
