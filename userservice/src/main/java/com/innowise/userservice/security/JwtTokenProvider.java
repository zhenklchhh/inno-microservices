package com.innowise.userservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Component
public class JwtTokenProvider {
    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    public Authentication getAuthentication(String token) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
        String login = jwt.getSubject();
        List<String> roles = jwt.getClaim("roles").asList(String.class);
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        return new UsernamePasswordAuthenticationToken(login, null, authorities);
    }

    public boolean validateToken(String token) {
        try{
            JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
            return true;
        } catch(JWTVerificationException e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
