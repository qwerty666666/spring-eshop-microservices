package com.example.eshop.rest.controllers.base;

import com.example.eshop.auth.CustomJwtAuthentication;
import com.example.eshop.rest.utils.AuthUtils;

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
    protected CustomJwtAuthentication getAuthenticatedUserDetailsOrFail() {
        return AuthUtils.getCurrentUserDetails()
                .orElseThrow(() -> new NotAuthenticatedException("User is not authenticated"));
    }
}
