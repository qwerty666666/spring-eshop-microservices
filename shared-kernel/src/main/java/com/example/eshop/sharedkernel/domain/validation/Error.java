package com.example.eshop.sharedkernel.domain.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Error {
    private final String field;
    private final String message;
}
