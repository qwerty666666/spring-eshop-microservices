package com.example.eshop.rest.models;

import java.util.Objects;

public record FieldErrorDto(
        String field,
        String message
) {
    public FieldErrorDto(String field, String message) {
        this.field = Objects.requireNonNull(field);
        this.message = message == null ? "" : message;
    }
}
