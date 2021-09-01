package com.example.eshop.sharedkernel.domain.valueobject;

import lombok.Getter;

@Getter
public class InvalidEanFormatException extends RuntimeException {
    private String ean;

    public InvalidEanFormatException(String ean, String message) {
        super(message);
        this.ean = ean;
    }
}
