package com.example.eshop.rest.controllers.utils;

import com.example.eshop.rest.dto.FieldErrorDto;
import com.example.eshop.rest.dto.ValidationErrorDto;

public class ValidationErrorBuilder {
    private final ValidationErrorDto errors = new ValidationErrorDto();

    private ValidationErrorBuilder() {
    }

    public static ValidationErrorBuilder newInstance() {
        return new ValidationErrorBuilder();
    }

    public ValidationErrorDto build() {
        return errors;
    }

    public ValidationErrorBuilder addError(String field, String message) {
        var error = new FieldErrorDto();
        error.setField(field);
        error.setMessage(message);

        errors.getErrors().add(error);

        return this;
    }
}
