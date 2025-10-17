package com.innowise.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Evgeniy Zaleshchenok
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(String message) {
        super(message);
    }
    public CardNotFoundException(Long id) {
        super("Card with id " + id + " not found");
    }
    public CardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
