package com.example.eshop.sharedkernel.domain.validation;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Single field validation error
 */
@Getter
@EqualsAndHashCode
public class FieldError {
    private final String field;
    private final String messageCode;
    private final Object[] messageParams;

    public FieldError(String field, String messageCode, Object... messageParams) {
        this.field = field;
        this.messageCode = messageCode;
        this.messageParams = messageParams;
    }

    @Override
    public String toString() {
        return "Error: field - %s, messageCode - %s".formatted(field, messageCode);
    }
}
