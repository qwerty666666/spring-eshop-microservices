package com.example.eshop.localizer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Localizer returning message from Spring's {@link MessageSource}
 */
@RequiredArgsConstructor
public class MessageSourceLocalizer implements Localizer {
    private final MessageSource messageSource;
    private final Supplier<Locale> defaultLocaleProducer;

    @Override
    public String getMessage(String code, Object... params) {
        return getMessage(code, defaultLocaleProducer.get(), params);
    }

    @Override
    public String getMessage(String code, Locale locale, Object... params) {
        return messageSource.getMessage(code, params, locale);
    }
}
