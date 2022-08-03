package com.example.eshop.checkout.config;

import com.example.eshop.checkout.rest.utils.RestProperties;
import com.example.eshop.checkout.rest.utils.UriUtils;
import com.example.eshop.localizer.LocalizerAutoConfiguration;
import com.example.eshop.restutils.converters.ConvertersAutoConfiguration;
import com.example.eshop.restutils.errorhandling.MvcErrorHandlersAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ AuthConfig.class, UriUtils.class })
@EnableConfigurationProperties({ RestProperties.class })
@ImportAutoConfiguration({
        LocalizerAutoConfiguration.class,
        MvcErrorHandlersAutoConfiguration.class,
        ConvertersAutoConfiguration.class
})
public class ControllerTestsConfig {
}
