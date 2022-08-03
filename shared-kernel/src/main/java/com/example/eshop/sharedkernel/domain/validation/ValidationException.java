package com.example.eshop.sharedkernel.domain.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ValidationException extends RuntimeException {
    private final Errors errors;
}
