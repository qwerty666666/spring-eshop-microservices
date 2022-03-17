package com.example.eshop.restutils.converters;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * Auto-configuration that registers {@link Converter}s
 */
@Configuration
public class ConvertersAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public Converter<String, Ean> eanConverter() {
        return new EanConverter();
    }
}
