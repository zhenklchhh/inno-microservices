package com.innowise.innomicroservices.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
