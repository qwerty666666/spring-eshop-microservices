package com.example.eshop.order.config;

import com.example.eshop.localizer.Localizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import static org.mockito.Mockito.mock;

@TestConfiguration
@ComponentScan("com.example.eshop.order.rest.mappers")
public class MapperTestsConfig {
    @Bean
    public Localizer localizer() {
        return mock(Localizer.class);
    }
}
