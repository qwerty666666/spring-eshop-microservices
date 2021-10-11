package com.example.eshop.rest.controllers.base;

import com.example.eshop.customer.infrastructure.auth.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

/**
 * Base class for all Controllers.
 * <p>
 * We use it to provide common methods for controllers, because
 * we can't use some Spring's functionality due to OpenAPI
 * autogeneration.
 */
public class BaseController {
    /**
     * @return currently authenticated principal
     */
    protected Optional<Authentication> getAuthentication() {
        return Optional.of(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Returns currently authenticated {@link UserDetailsImpl} or
     * throws {@link NotAuthenticatedException} if user is not authenticated.
     */
    protected UserDetailsImpl getAuthenticatedUserDetailsOrFail() {
        return getAuthentication()
                .map(auth -> (UserDetailsImpl)auth.getPrincipal())
                .orElseThrow(() -> new NotAuthenticatedException("User is not authenticated"));
    }
}
