package com.example.eshop.sharedkernel.domain;

public interface Localizer {
    /**
     * Returns message with default locale.
     */
    String getMessage(String code, Object... params);
}
