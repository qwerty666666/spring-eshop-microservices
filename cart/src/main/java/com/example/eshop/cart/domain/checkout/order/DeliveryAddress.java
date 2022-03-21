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
        @Nullable String flat
) {
    private static final DeliveryAddress NULL_ADDRESS = new DeliveryAddress(null, null, null, null, null, null, null);

    /**
     * @return empty address
     */
    public static DeliveryAddress nullAddress() {
        return NULL_ADDRESS;
    }
}
