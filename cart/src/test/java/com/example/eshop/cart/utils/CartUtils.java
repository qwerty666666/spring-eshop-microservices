package com.example.eshop.cart.utils;

import com.example.eshop.cart.domain.cart.Cart;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CartUtils {
    public static void assertCartsHasTheSameItems(Cart cart1, Cart cart2) {
        assertThat(cart1.getItems().size()).isEqualTo(cart2.getItems().size());

        for (var item1: cart1.getItems()) {
            assertThatNoException().isThrownBy(() -> cart2.getItem(item1.getEan()));

            var item2 = cart2.getItem(item1.getEan());

            assertThat(item1.getQuantity()).isEqualTo(item2.getQuantity());
        }
    }
}
