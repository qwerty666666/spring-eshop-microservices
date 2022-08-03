package com.example.eshop.apigateway.controllers;

import com.example.eshop.apigateway.config.AppProperties;
import com.example.eshop.apigateway.configs.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.CoreMatchers.containsString;

@WebFluxTest(SwaggerUiController.class)
@ControllerTest
@ActiveProfiles("test")
class SwaggerUiControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AppProperties appProperties;

    @Test
    void shouldReturnStatus200() {
        webTestClient.get()
                .uri(appProperties.getOas().getUrl())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(containsString("swagger-ui"));
    }
}
