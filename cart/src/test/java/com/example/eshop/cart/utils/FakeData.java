package com.example.eshop.cart.utils;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;

public class FakeData {
    public static DeliveryAddress deliveryAddress() {
        return new DeliveryAddress(fullname(), phone(), country(), city(), street(), building(), flat());
    }
    
    public static String fullname() {
        return "fullname";
    }

    public static String country() {
        return "country";
    }

    public static String city() {
        return "city";
    }

    public static String building() {
        return "building";
    }

    public static String street() {
        return "street";
    }

    public static String flat() {
        return "flat";
    }
    
    public static Phone phone() {
        return Phone.fromString("+79993334444");
    }

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

        cart.addItem(ean(), Money.USD(123), 10, "test");

        return cart;
    }

    public static Cart emptyCart() {
        return emptyCart(customerId());
    }

    public static Cart emptyCart(String customerId) {
        return new Cart(customerId);
    }
}
