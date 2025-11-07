package com.innowise.apigateway.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.JwtValidationException;
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

    private final String BEARER_STRING = "Bearer";

    @Value("${security.public-endpoints}")
    public List<String> publicEndpoints;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Predicate<ServerHttpRequest> predicate = r -> publicEndpoints.stream()
                .anyMatch(uri -> r.getURI().getPath().equals(uri));

        if(predicate.test(request)) {
            return chain.filter(exchange);
        }

        if(!request.getHeaders().containsKey("Authorization")) {
            return unauthorizedResponse(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith(BEARER_STRING)){
            return unauthorizedResponse(exchange);
        }

        String token = authHeader.substring("Bearer ".length());
        try{
            validateToken(token);
        } catch(JwtValidationException e){
            log.error(e.getMessage());
            return unauthorizedResponse(exchange);
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
