package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record RemoveCartItemCommand(
        @NotEmpty String customerId,
        @NotNull Ean ean) {
}
