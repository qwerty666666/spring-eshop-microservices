package com.example.eshop.rest.utils;

import com.example.eshop.customer.infrastructure.auth.UserDetailsImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthUtils {
    /**
     * @return currently authenticated principal
     */
    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.of(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * @return currently authenticated {@link UserDetailsImpl}
     */
    public static Optional<UserDetailsImpl> getCurrentUserDetails() {
        return getCurrentAuthentication()
                .filter(auth -> auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken))
                .map(auth -> (UserDetailsImpl)auth.getPrincipal());
    }
}
