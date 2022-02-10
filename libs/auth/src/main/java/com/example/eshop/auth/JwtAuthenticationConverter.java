package com.example.eshop.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import java.util.Collection;

/**
 * Converts {@link Jwt} to {@link CustomJwtAuthentication}.
 * <p>
 * This converter is commonly used in Spring's security configuration to
 * save custom {@link Authentication} in {@link SecurityContext} and to
 * consistently map authorities from jwt token.
 */
public class JwtAuthenticationConverter implements Converter<Jwt, CustomJwtAuthentication> {
    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Override
    public CustomJwtAuthentication convert(Jwt jwt) {
        var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
        return new CustomJwtAuthentication(jwt, authorities);
    }
}
