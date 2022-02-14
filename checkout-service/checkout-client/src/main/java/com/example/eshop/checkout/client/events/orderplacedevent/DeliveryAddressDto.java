package com.example.eshop.checkout.client.events.orderplacedevent;

import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import org.springframework.lang.Nullable;
import java.util.Objects;

public record DeliveryAddressDto(
        String fullname,
        Phone phone,
        String country,
        String city,
        @Nullable String street,
        String building,
        @Nullable String flat
) {
    public DeliveryAddressDto {
        Objects.requireNonNull(fullname, "fullname is required");
        Objects.requireNonNull(phone, "phone is required");
        Objects.requireNonNull(country, "country is required");
        Objects.requireNonNull(city, "city is required");
        Objects.requireNonNull(building, "building is required");
    }
}
