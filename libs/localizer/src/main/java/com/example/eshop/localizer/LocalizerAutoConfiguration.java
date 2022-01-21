package com.example.eshop.localizer;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import java.util.Locale;

@Configuration
@AutoConfigureAfter(MessageSourceAutoConfiguration.class)
public class LocalizerAutoConfiguration {
    /**
     * Localizer which delegates to {@link LocaleContextHolder} to
     * determine default {@link Locale}.
     */
    @Bean
    @ConditionalOnBean(MessageSource.class)
    public Localizer localizer(MessageSource messageSource) {
        return new MessageSourceLocalizer(messageSource, LocaleContextHolder::getLocale);
    }
}
