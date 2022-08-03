package com.example.eshop.cart.application.services.cartitem;

import com.example.eshop.cart.FakeData;
import com.example.eshop.catalog.client.model.SkuWithProductDto;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class AvailableQuantityRuleTest {
    @Test
    void whenRequestMoreQuantityThanIsAvailableForTheProduct_thenThrowNotEnoughQuantityException() {
        // Given
        var customerId = "customerId";
        var ean = FakeData.ean();
        var requestedQuantity = 1;
        var availableQuantity = 0;

        var cart = FakeData.emptyCart(customerId);
        var command = new AddCartItemCommand(customerId, ean, requestedQuantity);
        var sku = SkuWithProductDto.builder()
                .quantity(availableQuantity)
                .build();

        var rule = new AvailableQuantityRule();

        // When + Then
        var exception = catchThrowableOfType(() -> rule.check(cart, command, sku),
                AddToCartRuleViolationException.class);

        assertThat(exception.getCause()).isInstanceOf(NotEnoughQuantityException.class);
        assertThat(((NotEnoughQuantityException) exception.getCause()).getRequiredQuantity()).isEqualTo(requestedQuantity);
        assertThat(((NotEnoughQuantityException) exception.getCause()).getAvailableQuantity()).isEqualTo(availableQuantity);
    }

    @Test
    void whenRequestLessQuantityThanIsAvailableForTheProduct_thenNoExceptionIsThrown() {
        // Given
        var customerId = "customerId";
        var ean = FakeData.ean();
        var requestedQuantity = 1;

        var cart = FakeData.emptyCart(customerId);
        var command = new AddCartItemCommand(customerId, ean, requestedQuantity);
        var sku = SkuWithProductDto.builder()
                .quantity(requestedQuantity)
                .build();

        var rule = new AvailableQuantityRule();

        // When + Then
        assertThatNoException().isThrownBy(() -> rule.check(cart, command, sku));
    }
}