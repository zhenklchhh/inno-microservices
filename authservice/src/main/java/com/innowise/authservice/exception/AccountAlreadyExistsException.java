package com.innowise.authservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String login) {
        super(String.format("Account with login %s already exists", login));
    }
}
