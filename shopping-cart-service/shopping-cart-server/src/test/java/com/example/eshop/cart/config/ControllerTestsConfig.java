package com.example.eshop.cart.config;

import com.example.eshop.localizer.LocalizerAutoConfiguration;
import com.example.eshop.restutils.converters.ConvertersAutoConfiguration;
import com.example.eshop.restutils.errorhandling.MvcErrorHandlersAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@ImportAutoConfiguration({
        LocalizerAutoConfiguration.class,
        ConvertersAutoConfiguration.class,
        MvcErrorHandlersAutoConfiguration.class
})
public class ControllerTestsConfig {
}
