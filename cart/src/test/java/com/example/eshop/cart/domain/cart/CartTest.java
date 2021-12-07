package com.example.eshop.cart.domain.cart;

import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {
    private final Cart cart = FakeData.cart();
    private final Ean existedInCartEan = cart.getItems().get(0).getEan();
    private final Ean newEan = Ean.fromString("0799439112766");

    private final Money price = Money.USD(10);
    private final int qty = 10;

    @Nested
    class AddItemTest {
        @Test
        void whenAddItem_thenNewCartItemShouldBeCreatedAndAddedToCart() {
            // Given
            var expectedCartItem = new CartItem(cart, newEan, price, qty);

            // When
            cart.addItem(newEan, price, qty);

            // Then
            assertThat(cart.containsItem(newEan)).isTrue();
            assertThat(cart.getItems()).hasSize(2);
            assertThat(cart.getItems().get(1)).isEqualTo(expectedCartItem);
        }

        @Test
        void givenCartWithItem_whenAddTheSameItem_thenThrowCartItemAlreadyExistException() {
            assertThatThrownBy(() -> cart.addItem(existedInCartEan, price, qty))
                    .isInstanceOf(CartItemAlreadyExistException.class);
        }
    }

    @Nested
    class ChangeItemQuantityTest {
        @Test
        void whenChangeItemQuantity_thenCartItemQuantityIsUpdated() {
            // Given
            int newQty = 32;

            // When
            cart.changeItemQuantity(existedInCartEan, newQty);

            // Then
            assertThat(cart.getItem(existedInCartEan).getQuantity()).isEqualTo(newQty);
        }

        @Test
        void givenNonExistingItem_whenChangeItemQuantity_thenThrowCartItemNotFoundException() {
            assertThatThrownBy(() -> cart.changeItemQuantity(newEan, 123))
                    .isInstanceOf(CartItemNotFoundException.class);
        }
    }

    @Nested
    class RemoveItemTest {
        @Test
        void whenRemove_thenCartItemWithGivenEanRemoveFromItems() {
            // When
            cart.removeItem(existedInCartEan);

            // Then
            assertThat(cart.containsItem(existedInCartEan)).isFalse();
        }

        @Test
        void givenNonExistingInCartEan_whenRemove_thenThrowCartItemNotFoundException() {
            assertThatThrownBy(() -> cart.removeItem(newEan))
                    .isInstanceOf(CartItemNotFoundException.class);
        }
    }

    @Test
    void whenClear_thenCartShouldHasNoItems() {
        // When
        cart.clear();

        // Then
        assertThat(cart.getItems()).isEmpty();
    }
}
