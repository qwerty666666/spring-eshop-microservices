package com.example.eshop.sales.application.services.createorder;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import org.springframework.lang.Nullable;
import java.util.List;
import java.util.UUID;

public record CreateOrderDto(
        UUID id,
        String customerId,
        String paymentId,
        String deliveryId,
        AddressDto address,
        List<OrderLineDto> lines) {
    public static record AddressDto(
            String fullname,
            Phone phone,
            String country,
            String city,
            @Nullable String street,
            String building,
            @Nullable String flat) {
    }

    public static record OrderLineDto(
            Ean ean,
            int quantity,
            Money price) {
    }
}
