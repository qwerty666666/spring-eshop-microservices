package com.example.eshop.rest.config;

import com.example.eshop.catalog.client.CatalogService;
import com.example.eshop.localizer.Localizer;
import com.example.eshop.rest.AppProperties;
import com.example.eshop.rest.utils.UriUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan("com.example.eshop.rest.mappers")
public class MapperTestsConfig {
    @Bean
    public CatalogService catalogService() {
        return mock(CatalogService.class);
    }

    @Bean
    public UriUtils uriBuilder() {
        return new UriUtils(new AppProperties());
    }

    @Bean
    public Localizer localizer() {
        return mock(Localizer.class);
    }
}
