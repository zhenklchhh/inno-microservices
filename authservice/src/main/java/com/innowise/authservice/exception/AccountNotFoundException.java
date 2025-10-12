package com.innowise.authservice.exception;

/**
 * @author Evgeniy Zaleshchenok
 */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String login) {
        super(String.format("Account with login %s not found", login));
    }
}
