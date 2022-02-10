package com.example.eshop.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.util.Collection;

/**
 * Extension of {@link JwtAuthenticationToken}.
 * <p>
 * The purpose of this class is to provide convenient methods
 * for accessing claims from jwt token, that can be used across
 * all microservices consistently.
 */
public class CustomJwtAuthentication extends JwtAuthenticationToken {
    public CustomJwtAuthentication(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
    }

    public String getCustomerId() {
        return getToken().getClaimAsString(JwtClaimNames.SUB);
    }

    public String getEmail() {
        return getToken().getClaimAsString("email");
    }
}
