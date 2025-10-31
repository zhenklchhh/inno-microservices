package com.innowise.apigateway.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author Evgeniy Zaleshchenok
 */
@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter {
    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    public static final List<String> publicEndpoints = List.of(
            "/auth/register",
            "/auth/login"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Predicate<ServerHttpRequest> predicate = r -> publicEndpoints.stream()
                .anyMatch(uri -> r.getURI().getPath().contains(uri));

        if(predicate.test(request)) {
            return chain.filter(exchange);
        }

        if(!request.getHeaders().containsKey("Authorization")) {
            return unauthorizedResponse(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try{
                validateToken(token);
            } catch (JWTVerificationException e) {
                log.error(e.getMessage());
                return unauthorizedResponse(exchange);
            }
        }
        return chain.filter(exchange);
    }

    public String validateToken(String token) {
        return JWT.require(Algorithm.HMAC256(secretKey))
                .build()
                .verify(token)
                .getSubject();
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }
}
