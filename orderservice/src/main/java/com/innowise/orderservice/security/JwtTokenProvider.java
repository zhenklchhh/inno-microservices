package com.innowise.orderservice.security;

import com.auth0.jwt.JWT;
import org.springframework.stereotype.Component;

/**
 * @author Evgeniy Zaleshchenok
 */
@Component
public class JwtTokenProvider {
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
