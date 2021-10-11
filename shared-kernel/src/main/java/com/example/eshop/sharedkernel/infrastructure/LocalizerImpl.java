package com.example.eshop.sharedkernel.infrastructure;

import com.example.eshop.sharedkernel.domain.Localizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalizerImpl implements Localizer {
    private final MessageSource messageSource;

    @Override
    public String getMessage(String code, Object... params) {
        return messageSource.getMessage(code, params, LocaleContextHolder.getLocale());
    }
}
