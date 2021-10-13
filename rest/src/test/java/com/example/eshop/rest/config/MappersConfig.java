package com.example.eshop.rest.config;

import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.rest.AppProperties;
import com.example.eshop.rest.utils.UriBuilder;
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

    @Primary
    @Bean
    public UriBuilder uriBuilder() {
        return new UriBuilder(new AppProperties());
    }
}
