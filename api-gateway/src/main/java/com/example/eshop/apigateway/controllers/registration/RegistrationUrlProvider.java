package com.example.eshop.apigateway.controllers.registration;

import java.net.URI;

@FunctionalInterface
public interface RegistrationUrlProvider {
    /**
     * Generates URL to registration page.
     *
     * @param redirectUrl the URL to which user should be redirected after
     *                    successful registration
     */
    URI buildRegistrationUrl(String redirectUrl);
}
