package com.example.eshop.sharedkernel.domain.validation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Single field validation error
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Error {
    private final String field;
    private final String messageCode;
    private final Object[] messageParams;
}
