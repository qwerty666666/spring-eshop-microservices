package com.example.eshop.cart.domain.cart;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {
    @Test
    void whenAddItem_thenNewCartItemShouldBeCreatedAndAddedToCart() {
        // Given
        var cart = new Cart("1");
        Ean ean = Ean.fromString("0799439112766");
        var qty = 10;

        var expectedCartItem = new CartItem(cart, ean, qty);

        // When
        cart.addItem(ean, qty);

        // Then
        assertThat(cart.containsItem(ean)).isTrue();
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().iterator().next()).isEqualTo(expectedCartItem);
    }

    @Test
    void givenCartWithItem_whenAddTheSameItem_thenThrowCartItemAlreadyExistException() {
        // Given
        var cart = new Cart("1");
        Ean ean = Ean.fromString("0799439112766");
        var qty = 10;

        // When + Then
        cart.addItem(ean, qty);
        assertThatThrownBy(() -> cart.addItem(ean, qty))
                .isInstanceOf(CartItemAlreadyExistException.class);
    }

    @Test
    void whenChangeItemQuantity_thenCartItemQuantityIsUpdated() {
        // Given
        Ean ean = Ean.fromString("0799439112766");
        var qty = 10;
        var newQty = 10;

        var cart = new Cart("1");
        cart.addItem(ean, qty);

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
        Ean ean = Ean.fromString("0799439112766");
        var qty = 10;

        // When + Then
        assertThatThrownBy(() -> cart.changeItemQuantity(ean, qty))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void whenRemove_thenCartItemWithGivenEanRemoveFromItems() {
        // Given
        var removingEan = Ean.fromString("0799439112766");

        var cart = new Cart("1");
        cart.addItem(removingEan, 10);
        cart.addItem(Ean.fromString("1111111111111"), 10);

        // When
        cart.removeItem(removingEan);

        // Then
        assertThat(cart.getItems()).extracting(CartItem::getEan).doesNotContain(removingEan);
    }

    @Test
    void givenNonExistingInCartEan_whenRemove_thenThrowCartItemNotFoundException() {
        // Given
        var cart = new Cart("1");
        Ean ean = Ean.fromString("0799439112766");

        // When + Then
        assertThatThrownBy(() -> cart.removeItem(ean))
                .isInstanceOf(CartItemNotFoundException.class);
    }
}
