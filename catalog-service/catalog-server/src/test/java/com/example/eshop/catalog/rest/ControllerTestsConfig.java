package com.example.eshop.catalog.rest;

import com.example.eshop.catalog.config.AppProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ MappersConfig.class })
@EnableConfigurationProperties({ AppProperties.class })
public class ControllerTestsConfig {
}
