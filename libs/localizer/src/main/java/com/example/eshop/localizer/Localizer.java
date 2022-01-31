package com.example.eshop.localizer;

import java.util.Locale;

public interface Localizer {
    /**
     * Returns message with default locale.
     */
    String getMessage(String code, Object... params);

    /**
     * Returns message with given {@code Locale}.
     */
    String getMessage(String code, Locale locale, Object... params);
}
