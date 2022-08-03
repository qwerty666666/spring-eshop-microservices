package com.example.eshop.cart;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FakeData {
    public static String customerId() {
        return "customerId";
    }

    public static Ean ean() {
        return Ean.fromString("1234567890123");
    }

    public static Cart cart() {
        return cart(customerId());
    }

    public static Cart cart(String customerId) {
        var cart = new Cart(customerId);

        cart.addItem(ean(), Money.USD(123), 10);

        return cart;
    }

    public static Cart emptyCart(String customerId) {
        return new Cart(customerId);
    }
}
