package com.example.eshop.sharedkernel.domain.validation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Error implements Serializable {
    private final String field;
    private final String message;
}
