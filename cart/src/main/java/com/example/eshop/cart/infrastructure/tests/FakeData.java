package com.example.eshop.cart.infrastructure.tests;

import com.example.eshop.cart.client.model.AttributeDto;
import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.cart.client.model.CartItemDto;
import com.example.eshop.cart.client.model.ImageDto;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

        cart.addItem(ean(), Money.USD(123), 10);

        return cart;
    }

    public static Cart emptyCart(String customerId) {
        return new Cart(customerId);
    }

    public static CartDto emptyCartDto() {
        return new CartDto()
                .id("1")
                .totalPrice(Money.ZERO);
    }

    public static CartDto cartDto() {
        var skuPrice = Money.USD(10);
        var quantity = 2;
        var cartPrice = Money.USD(20);

        return new CartDto()
                .id("1")
                .totalPrice(cartPrice)
                .items(List.of(
                        new CartItemDto()
                                .ean(ean())
                                .price(skuPrice)
                                .quantity(quantity)
                                .availableQuantity(quantity)
                                .productName("productName")
                                .attributes(List.of(
                                        new AttributeDto("1", "size", "42")
                                ))
                                .images(List.of(
                                        new ImageDto("url")
                                ))
                ));
    }
}
