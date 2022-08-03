package com.example.eshop.apigateway.controllers;

import com.example.eshop.apigateway.controllers.registration.RegistrationUrlProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RegisterController {
    /**
     * Registration Endpoint.
     * <p>
     * Redirect to IdP registration URL. After successful registration redirects to home page.
     */
    @Bean
    public RouterFunction<ServerResponse> registrationEndpoint(RegistrationUrlProvider registrationUrlProvider) {
        return RouterFunctions.route(RequestPredicates.GET("/register"), request -> {
            var redirectUrl = getHomePageUrl(request);
            var registrationUrl = registrationUrlProvider.buildRegistrationUrl(redirectUrl);

            return ServerResponse.temporaryRedirect(registrationUrl)
                    .build();
        });
    }

    /**
     * Get URL to index page from given request
     */
    private String getHomePageUrl(ServerRequest request) {
        var currentUri = request.exchange().getRequest().getURI();

        return "%s://%s".formatted(currentUri.getScheme(), currentUri.getAuthority());
    }
}
