package com.example.eshop.cart.domain.cart;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {
    private final Ean ean = Ean.fromString("0799439112766");
    private final Money price = Money.USD(10);
    private final int qty = 10;
    private final String productName = "test";

    @Test
    void whenAddItem_thenNewCartItemShouldBeCreatedAndAddedToCart() {
        // Given
        var cart = new Cart("1");

        var expectedCartItem = new CartItem(cart, ean, price, qty, productName);

        // When
        cart.addItem(ean, price, qty, productName);

        // Then
        assertThat(cart.containsItem(ean)).isTrue();
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().iterator().next()).isEqualTo(expectedCartItem);
    }

    @Test
    void givenCartWithItem_whenAddTheSameItem_thenThrowCartItemAlreadyExistException() {
        // Given
        var cart = new Cart("1");

        // When + Then
        cart.addItem(ean, price, qty, productName);
        assertThatThrownBy(() -> cart.addItem(ean, price, qty, productName))
                .isInstanceOf(CartItemAlreadyExistException.class);
    }

    @Test
    void whenChangeItemQuantity_thenCartItemQuantityIsUpdated() {
        // Given
        Ean ean = Ean.fromString("0799439112766");
        var newQty = 10;

        var cart = new Cart("1");
        cart.addItem(ean, price, qty, productName);

        var item = cart.getItems().iterator().next();

        // When
        cart.changeItemQuantity(ean, newQty);

        // Then
        assertThat(item.getQuantity()).isEqualTo(newQty);
        assertThat(cart.getItems()).containsOnly(item);
    }

    @Test
    void givenNonExistingItem_whenChangeItemQuantity_thenThrowCartItemNotFoundException() {
        // Given
        var cart = new Cart("1");

        // When + Then
        assertThatThrownBy(() -> cart.changeItemQuantity(ean, 123))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void whenRemove_thenCartItemWithGivenEanRemoveFromItems() {
        // Given
        var cart = new Cart("1");
        cart.addItem(ean, price, qty, productName);
        cart.addItem(Ean.fromString("1111111111111"), price, qty, productName);

        // When
        cart.removeItem(ean);

        // Then
        assertThat(cart.getItems()).extracting(CartItem::getEan).doesNotContain(ean);
    }

    @Test
    void givenNonExistingInCartEan_whenRemove_thenThrowCartItemNotFoundException() {
        // Given
        var cart = new Cart("1");

        // When + Then
        assertThatThrownBy(() -> cart.removeItem(ean))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void whenClear_thenCartShouldHasNoItems() {
        // Given
        var cart = new Cart("1");
        cart.addItem(ean, price, qty, productName);

        // When
        cart.clear();

        // Then
        assertThat(cart.getItems()).isEmpty();
    }
}
