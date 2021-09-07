package com.example.eshop.sharedkernel.domain.validation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Error {
    private final String field;
    private final String message;
}
