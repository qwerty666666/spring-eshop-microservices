package com.example.eshop.apigateway.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Configs for OAuth2 Client
 */
@Configuration
public class Oauth2Config {
    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
            ServerCodecConfigurer serverCodecConfigurer) {
        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
                ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .password()
                        .build();

        DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        authorizedClientManager.setContextAttributesMapper(contextAttributesMapper(serverCodecConfigurer));

        return authorizedClientManager;
    }

    /**
     * Maps credentials from request to {@link OAuth2AuthorizationContext} attributes.
     * These attributes are used to authorize {@link OAuth2AuthorizedClient}.
     */
    private Function<OAuth2AuthorizeRequest, Mono<Map<String, Object>>> contextAttributesMapper(
            ServerCodecConfigurer serverCodecConfigurer) {
        // takes `username` and `password` parameters from request body

        return authorizeRequest -> {
            ServerWebExchange exchange = authorizeRequest.getAttribute(ServerWebExchange.class.getName());
            var request = ServerRequest.create(exchange, serverCodecConfigurer.getReaders());

            return request
                    .bodyToMono(new ParameterizedTypeReference<MultiValueMap<String, String>>() {})
                    .map(parameters -> {
                        var username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
                        var password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);

                        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
                            return Collections.emptyMap();
                        }

                        Map<String, Object> contextAttributes = new HashMap<>();

                        // `PasswordReactiveOAuth2AuthorizedClientProvider` requires both attributes
                        contextAttributes.put(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, username);
                        contextAttributes.put(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, password);

                        return contextAttributes;
                    });
        };
    }
}
