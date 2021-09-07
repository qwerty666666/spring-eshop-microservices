package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record UpsertCartItemCommand(
        @NotEmpty String customerId,
        @NotNull Ean ean,
        @Positive int quantity) {
}
