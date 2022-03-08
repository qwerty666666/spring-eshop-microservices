package com.example.eshop.cart.rest.utils;

import com.example.eshop.cart.client.api.model.FieldErrorDto;
import com.example.eshop.cart.client.api.model.ValidationErrorDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Builder for {@link ValidationErrorDto}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationErrorBuilder {
    private final ValidationErrorDto errors = new ValidationErrorDto();

    public static ValidationErrorBuilder newInstance() {
        return new ValidationErrorBuilder();
    }

    public ValidationErrorDto build() {
        return errors;
    }

    public ValidationErrorBuilder addError(String field, String message) {
        var error = new FieldErrorDto()
                .field(field)
                .message(message);

        errors.getErrors().add(error);

        return this;
    }
}
