package com.example.eshop.cart.domain.checkout.order;

import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import org.springframework.lang.Nullable;

public record DeliveryAddress(
        @Nullable String fullname,
        @Nullable Phone phone,
        @Nullable String country,
        @Nullable String city,
        @Nullable String street,
        @Nullable String building,
        @Nullable String flat) {

    public DeliveryAddress() {
        this(null, null, null, null, null, null, null);
    }
}
