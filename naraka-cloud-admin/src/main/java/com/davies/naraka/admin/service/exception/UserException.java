package com.davies.naraka.admin.service.exception;

/**
 * @author davies
 * @date 2022/1/24 12:57 PM
 */
public class UserException extends SystemVerifyException{

    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserException(Throwable cause) {
        super(cause);
    }


}
