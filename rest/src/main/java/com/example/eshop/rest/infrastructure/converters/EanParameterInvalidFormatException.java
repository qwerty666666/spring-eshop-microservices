package com.example.eshop.rest.infrastructure.converters;

import lombok.Getter;

@Getter
public class EanParameterInvalidFormatException extends RuntimeException {
    private String ean;

    public EanParameterInvalidFormatException(String ean) {
        this.ean = ean;
    }
}
