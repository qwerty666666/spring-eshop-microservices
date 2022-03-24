package com.example.eshop.apigateway.controllers;

import com.example.eshop.localizer.Localizer;
import com.example.eshop.rest.models.BasicErrorDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken("anonymous",
            "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

    private final ReactiveOAuth2AuthorizedClientManager authorizedClientManager;
    private final Localizer localizer;

    /**
     * Generate new access_token for user
     */
    @PostMapping("/token")
    public Mono<ResponseEntity> index(ServerWebExchange exchange) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                .principal(ANONYMOUS_AUTHENTICATION)
                // this attribute is used in SecurityConfig#contextAttributesMapper to extract
                // username / password to oauth2 authorize request context attributes
                .attribute(ServerWebExchange.class.getName(), exchange)
                .build();

        return this.authorizedClientManager.authorize(authorizeRequest)
                .map(token -> accessTokenResponse(token.getAccessToken().getTokenValue()))
                // there is empty token response if `username` or `password` are 
                // not presented in ContextAttributes and PasswordReactiveOAuth2AuthorizedClientProvider
                // do not try to authorize client
                .switchIfEmpty(Mono.just(invalidUserInputResponse()))
                // check if user credentials are invalid
                .onErrorResume(this::isInvalidGrantException, e ->  Mono.just(invalidUserCredentialsResponse()));
    }

    /**
     * @return response with token
     */
    private ResponseEntity accessTokenResponse(String accessToken) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new TokenResponse(accessToken));
    }

    /**
     * @return response if user credentials are invalid
     */
    private ResponseEntity invalidUserCredentialsResponse() {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BasicErrorDto(400, localizer.getMessage("token_invalidUserCredentials")));
    }

    /**
     * @return response if request parameters are invalid
     */
    private ResponseEntity invalidUserInputResponse() {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BasicErrorDto(400, localizer.getMessage("token_missedRequiredParameters")));
    }

    /**
     * @return if {@code e} is exception indicating that user credentials
     *         in password grant flow are invalid
     */
    private boolean isInvalidGrantException(Throwable e) {
        return (e instanceof ClientAuthorizationException clientException) &&
                clientException.getError().getErrorCode().equals(OAuth2ErrorCodes.INVALID_GRANT);
    }

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
}
