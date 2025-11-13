package com.innowise.orderservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(Long id) {
        super("Item with id " + id + " not found");
    }

    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
