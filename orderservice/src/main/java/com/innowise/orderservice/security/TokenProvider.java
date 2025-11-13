package com.innowise.orderservice.security;

/**
 * @author Evgeniy Zaleshchenok
 */
public interface TokenProvider {
    String getEmailFromToken(String token);
}
