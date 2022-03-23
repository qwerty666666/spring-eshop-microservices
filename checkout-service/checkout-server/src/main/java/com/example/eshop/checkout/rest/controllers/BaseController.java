package com.example.eshop.checkout.rest.controllers;

import com.example.eshop.auth.AuthUtils;
import com.example.eshop.auth.CustomJwtAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base class for all Controllers.
 * <p>
 * We use it to provide common methods for controllers, because
 * we can't use some Spring's functionality due to OpenAPI
 * autogeneration.
 */
public class BaseController {
    /**
     * Handle exception if current user is unauthorized
     */
    @ExceptionHandler(NotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private void handleNotAuthenticatedException() {
        // Unauthorized response should be returned by SecurityConfig.
        // This method is for safety purpose only to return 401 instead of 500
        // in the case of misconfiguration
    }

    /**
     * Returns currently authenticated {@link CustomJwtAuthentication} or
     * throws {@link NotAuthenticatedException} if user is not authenticated.
     */
    protected CustomJwtAuthentication getCurrentAuthenticationOrFail() {
        return AuthUtils.getCurrentUserDetails()
                .orElseThrow(() -> new NotAuthenticatedException("User is not authenticated"));
    }
}
