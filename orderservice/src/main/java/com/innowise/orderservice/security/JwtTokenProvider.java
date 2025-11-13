package com.innowise.orderservice.security;

import com.auth0.jwt.JWT;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author Evgeniy Zaleshchenok
 */
@Component
@Profile("!test")
public class JwtTokenProvider implements TokenProvider {
    @Override
    public String getEmailFromToken(String authHeader){
        String token = resolveToken(authHeader);
        return JWT.decode(token).getSubject();
    }

    public String resolveToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
