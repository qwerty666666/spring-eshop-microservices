package com.example.eshop.cart.config;

import com.example.eshop.catalog.client.CatalogServiceClient;
import com.example.eshop.localizer.Localizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import static org.mockito.Mockito.mock;

@TestConfiguration
@ComponentScan("com.example.eshop.cart.rest.mappers")
public class MapperTestsConfig {
    @Bean
    public CatalogServiceClient catalogService() {
        return mock(CatalogServiceClient.class);
    }

    @Bean
    public Localizer localizer() {
        return mock(Localizer.class);
    }
}
