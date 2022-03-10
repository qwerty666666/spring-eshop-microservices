package com.example.eshop.catalog.configs;

import com.example.eshop.localizer.LocalizerAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({ MappersConfig.class, AppPropertiesTestConfig.class })
@ImportAutoConfiguration({ LocalizerAutoConfiguration.class })
public class ControllerTestsConfig {
}
