package com.example.eshop.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthUtils {
    private static final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    /**
     * @return currently authenticated principal
     */
    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.of(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * @return currently authenticated {@link CustomJwtAuthentication}
     */
    public static Optional<CustomJwtAuthentication> getCurrentUserDetails() {
        return getCurrentAuthentication()
                .filter(auth -> auth.isAuthenticated() && !authenticationTrustResolver.isAnonymous(auth))
                .map(CustomJwtAuthentication.class::cast);
    }
}
