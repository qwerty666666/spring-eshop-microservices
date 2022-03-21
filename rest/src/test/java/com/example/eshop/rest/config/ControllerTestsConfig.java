package com.example.eshop.rest.config;

import com.example.eshop.localizer.LocalizerAutoConfiguration;
import com.example.eshop.rest.AppProperties;
import com.example.eshop.rest.utils.UriUtils;
import com.example.eshop.restutils.converters.ConvertersAutoConfiguration;
import com.example.eshop.restutils.errorhandling.MvcErrorHandlersAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ AuthConfig.class })
@EnableConfigurationProperties({ AppProperties.class })
@ImportAutoConfiguration({
        LocalizerAutoConfiguration.class,
        MvcErrorHandlersAutoConfiguration.class,
        ConvertersAutoConfiguration.class
})
public class ControllerTestsConfig {
    @Bean
    public UriUtils uriUtils(AppProperties appProperties) {
        return new UriUtils(appProperties);
    }
}
