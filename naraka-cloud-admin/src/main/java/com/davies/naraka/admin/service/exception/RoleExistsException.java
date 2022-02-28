package com.davies.naraka.admin.service.exception;

/**
 * @author davies
 * @date 2022/1/24 12:58 PM
 */
public class RoleExistsException extends RoleException{

    public RoleExistsException() {
        super();
    }

    public RoleExistsException(String message) {
        super(message);
    }

    public RoleExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoleExistsException(Throwable cause) {
        super(cause);
    }
}
