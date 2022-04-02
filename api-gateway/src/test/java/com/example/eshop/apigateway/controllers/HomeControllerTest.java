package com.example.eshop.apigateway.controllers;

import com.example.eshop.apigateway.config.AppProperties;
import com.example.eshop.apigateway.configs.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(HomeController.class)
@ControllerTest
@ActiveProfiles("test")
class HomeControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private AppProperties appProperties;

    @Test
    void homePageShouldRedirectToSwaggerUi() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location(appProperties.getOas().getUrl());
    }
}
