package com.example.eshop.checkout.client.order;

import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import org.springframework.lang.Nullable;

public record DeliveryAddressDto(
        String fullname,
        Phone phone,
        String country,
        String city,
        @Nullable String street,
        String building,
        @Nullable String flat
) {
}
