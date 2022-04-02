package com.example.eshop.apigateway.configs;

import com.example.eshop.apigateway.config.AppProperties;
import com.example.eshop.apigateway.config.security.SecurityFilterChainConfig;
import com.example.eshop.localizer.LocalizerAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@ImportAutoConfiguration({ LocalizerAutoConfiguration.class })
@Import({ SecurityFilterChainConfig.class })
@EnableConfigurationProperties({ AppProperties.class })
public class ControllerTestsConfig {
}
