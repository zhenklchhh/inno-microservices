package com.innowise.userservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
