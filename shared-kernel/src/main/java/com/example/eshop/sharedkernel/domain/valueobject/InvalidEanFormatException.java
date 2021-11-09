package com.example.eshop.sharedkernel.domain.valueobject;

import lombok.Getter;

@Getter
public class InvalidEanFormatException extends RuntimeException {
    private final String ean;

    public InvalidEanFormatException(String ean, String message) {
        super(message);
        this.ean = ean;
    }
}
