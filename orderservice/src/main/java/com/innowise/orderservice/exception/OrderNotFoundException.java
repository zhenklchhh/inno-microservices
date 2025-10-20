package com.innowise.orderservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(Long id) {
        super("Order with id " + id + " not found");
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
