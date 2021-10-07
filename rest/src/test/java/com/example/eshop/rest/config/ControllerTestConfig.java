package com.example.eshop.rest.config;

import com.example.eshop.rest.staticresources.ResourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({ AuthConfig.class, MappersConfig.class })
@EnableConfigurationProperties(ResourceProperties.class)
public class ControllerTestConfig {
}
