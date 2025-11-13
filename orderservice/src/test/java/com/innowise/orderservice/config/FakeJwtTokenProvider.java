package com.innowise.orderservice.config;

import com.innowise.orderservice.security.JwtTokenProvider;
import com.innowise.orderservice.security.TokenProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author Evgeniy Zaleshchenok
 */
@Component
@Profile("test")
public class FakeJwtTokenProvider implements TokenProvider {
    private String testUserEmail;

    public void setTestUserEmail(String email) {
        this.testUserEmail = email;
    }

    @Override
    public String getEmailFromToken(String token) {
        if (this.testUserEmail == null) {
            return "default.test.user@example.com";
        }
        return this.testUserEmail;
    }
}
