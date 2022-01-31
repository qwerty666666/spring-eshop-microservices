package com.example.eshop.catalog.configs;

import com.example.eshop.catalog.config.AppProperties;
import com.example.eshop.catalog.infrastructure.UriBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ComponentScan("com.example.eshop.catalog.rest.mappers")
@Import(UriBuilder.class)
@EnableConfigurationProperties({ AppProperties.class })
@ActiveProfiles("test")
public class MappersConfig {
}
