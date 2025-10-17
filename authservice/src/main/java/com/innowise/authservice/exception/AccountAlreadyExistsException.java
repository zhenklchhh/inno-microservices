package com.innowise.authservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String message) {
        super(message);
    }

    public AccountAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
