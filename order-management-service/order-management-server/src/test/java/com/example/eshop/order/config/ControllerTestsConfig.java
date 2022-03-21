package com.example.eshop.order.config;

import com.example.eshop.localizer.LocalizerAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({ AuthConfig.class, MapperTestsConfig.class })
@ImportAutoConfiguration({ LocalizerAutoConfiguration.class })
public class ControllerTestsConfig {
}
