package com.example.eshop.order.config;

import com.example.eshop.localizer.Localizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan("com.example.eshop.order.rest.mappers")
public class MapperTestsConfig {
    @Bean
    public Localizer localizer() {
        return mock(Localizer.class);
    }
}
