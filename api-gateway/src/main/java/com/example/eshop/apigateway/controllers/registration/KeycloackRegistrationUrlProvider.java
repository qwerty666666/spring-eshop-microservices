package com.example.eshop.apigateway.controllers.registration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.Map;

@Component
public class KeycloackRegistrationUrlProvider implements RegistrationUrlProvider {
    private static final String REDIRECT_URL_QUERY_PARAM = "redirectUrl";

    private final UriComponentsBuilder urlBuilder;

    public KeycloackRegistrationUrlProvider(
            @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}") String baseUrl,
            @Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String clientId
    ) {
        urlBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "protocol/openid-connect/auth")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("scope", "openid")
                .queryParam("redirect_uri", "{" + REDIRECT_URL_QUERY_PARAM + "}");
    }

    @Override
    public URI buildRegistrationUrl(String redirectUrl) {
        return urlBuilder.buildAndExpand(Map.of(REDIRECT_URL_QUERY_PARAM, redirectUrl))
                .toUri();
    }
}
