package com.example.eshop.rest.config;

import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.rest.staticresources.StaticResourceUriBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
@ComponentScan({
        "com.example.eshop.rest.mappers"
})
public class MappersConfig {
    @Bean
    public ProductCrudService productCrudService() {
        return mock(ProductCrudService.class);
    }

    @Bean
    @Primary
    public StaticResourceUriBuilder staticResourceUriBuilder() {
        return new StaticResourceUriBuilder() {
            @Override
            public String buildImageUri(String location) {
                return location;
            }
        };
    }
}
