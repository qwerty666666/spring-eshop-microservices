package com.example.eshop.order.rest.controllers;

import com.example.eshop.auth.AuthUtils;
import com.example.eshop.auth.CustomJwtAuthentication;

/**
 * Base class for all Controllers.
 * <p>
 * We use it to provide common methods for controllers, because
 * we can't use some Spring's functionality due to OpenAPI
 * autogeneration.
 */
public class BaseController {
    /**
     * Returns currently authenticated {@link CustomJwtAuthentication} or
     * throws {@link NotAuthenticatedException} if user is not authenticated.
     */
    protected CustomJwtAuthentication getCurrentAuthenticationOrFail() {
        return AuthUtils.getCurrentUserDetails()
                .orElseThrow(() -> new NotAuthenticatedException("User is not authenticated"));
    }
}
