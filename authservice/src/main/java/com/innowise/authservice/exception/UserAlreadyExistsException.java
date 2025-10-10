package com.innowise.authservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
