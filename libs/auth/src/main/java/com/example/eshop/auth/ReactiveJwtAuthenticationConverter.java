package com.example.eshop.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

/**
 * Converts {@link Jwt} to {@link CustomJwtAuthentication}.
 * <p>
 * This converter is commonly used in Spring's security configuration to
 * save custom {@link Authentication} in {@link SecurityContext} and to
 * consistently map authorities from jwt token.
 */
public class ReactiveJwtAuthenticationConverter implements Converter<Jwt, Mono<CustomJwtAuthentication>> {
    private final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

    @Override
    public Mono<CustomJwtAuthentication> convert(Jwt jwt) {
        return Mono.justOrEmpty(jwtAuthenticationConverter.convert(jwt));
    }
}
