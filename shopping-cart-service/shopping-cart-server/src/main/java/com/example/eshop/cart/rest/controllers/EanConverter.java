package com.example.eshop.cart.rest.controllers;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class EanConverter implements Converter<String, Ean> {
    @Nullable
    @Override
    public Ean convert(String source) {
        return Ean.fromString(source);
    }
}
