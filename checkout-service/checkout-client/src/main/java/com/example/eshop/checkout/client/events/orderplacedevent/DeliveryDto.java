package com.example.eshop.checkout.client.events.orderplacedevent;

import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.util.Objects;

public record DeliveryDto(
        DeliveryAddressDto address,
        DeliveryServiceDto deliveryService,
        Money price
) {
    public DeliveryDto {
        Objects.requireNonNull(address, "address is required");
        Objects.requireNonNull(deliveryService, "deliveryService is required");
        Objects.requireNonNull(price, "price is required");
    }
}
