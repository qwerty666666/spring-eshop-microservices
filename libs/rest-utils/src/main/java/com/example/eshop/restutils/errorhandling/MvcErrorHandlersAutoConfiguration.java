package com.example.eshop.restutils.errorhandling;

import com.example.eshop.localizer.MessageSourceLocalizer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@PropertySource("classpath:mvc_error_messages.properties")
public class MvcErrorHandlersAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ResponseEntityExceptionHandler responseEntityExceptionHandler() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("mvc_error_messages");

        var localizer = new MessageSourceLocalizer(messageSource, LocaleContextHolder::getLocale);

        return new ErrorControllerAdvice(localizer);
    }
}
