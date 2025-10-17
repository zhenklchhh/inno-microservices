package com.innowise.authservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.innowise.authservice.model.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
        String login = decodedJWT.getSubject();
        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        return new UsernamePasswordAuthenticationToken(login, "", authorities);
    }
}
