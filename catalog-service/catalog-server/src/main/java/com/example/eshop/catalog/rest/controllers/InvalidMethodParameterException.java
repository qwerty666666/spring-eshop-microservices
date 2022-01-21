package com.example.eshop.catalog.rest.controllers;

import com.example.eshop.sharedkernel.domain.validation.FieldError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Thrown inside controllers when there are errors in method
 * parameters.
 * <p>
 * We use this exception because we use OpenApi code autogeneration,
 * which generates Controller interfaces. And it restricts us with
 * method return types and method parameters validation and conversion.
 *
 * :D
 */
@RequiredArgsConstructor
@Getter
public class InvalidMethodParameterException extends RuntimeException {
    private final FieldError fieldError;
}
