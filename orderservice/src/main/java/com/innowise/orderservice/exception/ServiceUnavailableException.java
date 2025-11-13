package com.innowise.orderservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
      super(message, cause);
    }
}
