package com.example.eshop.apigateway.controllers;

import com.example.eshop.apigateway.config.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.CoreMatchers.containsString;

@SpringBootTest
@AutoConfigureWebTestClient
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
