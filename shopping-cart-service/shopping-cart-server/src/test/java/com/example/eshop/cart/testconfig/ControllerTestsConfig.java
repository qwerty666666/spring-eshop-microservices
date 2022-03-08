package com.example.eshop.cart.testconfig;

import com.example.eshop.localizer.LocalizerAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@ImportAutoConfiguration({ LocalizerAutoConfiguration.class })
public class ControllerTestsConfig {
}
