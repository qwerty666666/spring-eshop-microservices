package com.example.eshop.sharedkernel.domain.valueobject;

import lombok.Getter;

@Getter
public class InvalidPhoneFormatException extends RuntimeException {
    private final String phone;

    public InvalidPhoneFormatException(String phone, String message) {
        super(message);
        this.phone = phone;
    }
}
