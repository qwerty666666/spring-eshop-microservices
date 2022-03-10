package com.example.eshop.catalog.configs;

import com.example.eshop.catalog.config.AppProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@EnableConfigurationProperties({ AppProperties.class })
@Profile("test")
public class AppPropertiesTestConfig {
}
