package com.innowise.authservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class InvalidJwtAuthenticationException extends RuntimeException {
    public InvalidJwtAuthenticationException(String message) {
        super(message);
    }
}
