package com.example.eshop.apigateway.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

/**
 * Access token response for token Endpoint
 */
public record TokenResponse(
        @JsonProperty("access_token") String accessToken
) {
    public TokenResponse(OAuth2AccessToken token) {
        this(token.getTokenValue());
    }
}
