package com.innowise.authservice.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.innowise.authservice.model.entity.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
public class TokenProvider {
    @Value("${security.jwt.token.secret-key}")
    private String JWT_SECRET;

    public String generateAccessToken(Account account) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            return JWT.create()
                    .withSubject(account.getLogin())
                    .withClaim("login", account.getLogin())
                    .withExpiresAt(generateAccessExpirationTime())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
        }
    }

    public String validateAccessToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    public Instant generateAccessExpirationTime() {
        return LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
