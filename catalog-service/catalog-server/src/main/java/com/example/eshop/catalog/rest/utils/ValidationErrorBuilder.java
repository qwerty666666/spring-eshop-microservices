package com.example.eshop.catalog.rest.utils;

import com.example.eshop.catalog.client.api.model.FieldError;
import com.example.eshop.catalog.client.api.model.ValidationError;

public class ValidationErrorBuilder {
    private final ValidationError errors = new ValidationError();

    private ValidationErrorBuilder() {
    }

    public static ValidationErrorBuilder newInstance() {
        return new ValidationErrorBuilder();
    }

    public ValidationError build() {
        return errors;
    }

    public ValidationErrorBuilder addError(String field, String message) {
        var error = new FieldError();
        error.setField(field);
        error.setMessage(message);

        errors.getErrors().add(error);

        return this;
    }
}
