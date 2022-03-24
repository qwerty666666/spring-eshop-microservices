package com.example.eshop.apigateway.controllers;

import com.example.eshop.apigateway.config.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
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
