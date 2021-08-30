package com.example.eshop.rest.requests;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record PutItemToCartRequest(
        @NotNull Ean ean,
        @Positive int quantity) {
}
