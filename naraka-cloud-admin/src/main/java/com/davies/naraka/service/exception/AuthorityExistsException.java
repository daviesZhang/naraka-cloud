package com.davies.naraka.service.exception;

/**
 * @author davies
 * @date 2022/1/24 12:58 PM
 */
public class AuthorityExistsException extends SystemVerifyException{

    public AuthorityExistsException() {
    }

    public AuthorityExistsException(String message) {
        super(message);
    }

    public AuthorityExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorityExistsException(Throwable cause) {
        super(cause);
    }
}
