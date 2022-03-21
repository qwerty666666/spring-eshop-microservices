package com.example.eshop.catalog.configs;

import com.example.eshop.catalog.infrastructure.UriBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@TestConfiguration
@ComponentScan("com.example.eshop.catalog.rest.mappers")
@Import({ AppPropertiesTestConfig.class, UriBuilder.class })
public class MappersConfig {
}
