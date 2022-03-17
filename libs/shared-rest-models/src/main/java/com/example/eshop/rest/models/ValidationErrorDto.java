package com.example.eshop.rest.models;

import java.util.ArrayList;
import java.util.List;

public record ValidationErrorDto(
        List<FieldErrorDto> errors
) {
    public ValidationErrorDto() {
        this(new ArrayList<>());
    }

    public ValidationErrorDto addError(String field, String message) {
        return addError(new FieldErrorDto(field, message));
    }

    public ValidationErrorDto addError(FieldErrorDto error) {
        errors.add(error);
        return this;
    }
}
