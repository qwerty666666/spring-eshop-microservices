package com.example.eshop.catalog.rest;

import com.example.eshop.catalog.config.AppProperties;
import com.example.eshop.localizer.LocalizerAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ MappersConfig.class })
@ImportAutoConfiguration({ LocalizerAutoConfiguration.class })
@EnableConfigurationProperties({ AppProperties.class })
public class ControllerTestsConfig {
}
