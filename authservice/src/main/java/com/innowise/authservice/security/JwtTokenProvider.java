package com.innowise.authservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.innowise.authservice.model.entity.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
public class JwtTokenProvider {
    @Value("${security.jwt.token.secret-key}")
    private String secretKey;
    @Value("${security.jwt.token.expire-length.access-minutes}")
    private long accessTokenValidityInMinutes;

    @Value("${security.jwt.token.expire-length.refresh-days}")
    private long refreshTokenValidityInDays;

    public String generateAccessToken(Account account) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(account.getLogin())
                .withClaim("roles", List.of(account.getUserRole().name()))
                .withIssuedAt(now)
                .withExpiresAt(now.plus(accessTokenValidityInMinutes, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(secretKey));
    }

    public String generateRefreshToken(Account account) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(account.getLogin())
                .withIssuedAt(now)
                .withExpiresAt(now.plus(refreshTokenValidityInDays, ChronoUnit.DAYS))
                .sign(Algorithm.HMAC256(secretKey));
    }

    public String validateToken(String token) {
        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token)
                .getSubject();
    }

    public String getUsernameFromToken(String token) {
        return JWT.decode(token).getClaim("sub").asString();
    }
}
