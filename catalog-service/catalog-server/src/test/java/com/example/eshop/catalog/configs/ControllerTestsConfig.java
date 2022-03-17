package com.example.eshop.catalog.configs;

import com.example.eshop.localizer.LocalizerAutoConfiguration;
import com.example.eshop.restutils.converters.ConvertersAutoConfiguration;
import com.example.eshop.restutils.errorhandling.MvcErrorHandlersAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({
        MappersConfig.class,
        AppPropertiesTestConfig.class
})
@ImportAutoConfiguration({
        LocalizerAutoConfiguration.class,
        MvcErrorHandlersAutoConfiguration.class,
        ConvertersAutoConfiguration.class
})
public class ControllerTestsConfig {
}
