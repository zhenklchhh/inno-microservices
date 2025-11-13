package com.innowise.orderservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
