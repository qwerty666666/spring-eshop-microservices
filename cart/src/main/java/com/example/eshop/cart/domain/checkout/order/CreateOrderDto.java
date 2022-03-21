package com.example.eshop.cart.domain.checkout.order;

import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import lombok.Builder;
import org.springframework.lang.Nullable;

public record CreateOrderDto(
        String customerId,
        CartDto cart,
        DeliveryAddress address,
        @Nullable DeliveryServiceId deliveryServiceId,
        @Nullable PaymentServiceId paymentServiceId
) {
    public static final String CART_FIELD = "cart";
    public static final String DELIVERY_SERVICE_ID_FIELD = "deliveryServiceId";
    public static final String PAYMENT_SERVICE_ID_FIELD = "paymentServiceId";
    public static final String ADDRESS_FIELD = "address";
    public static final String CUSTOMER_ID_FIELD = "customerId";

    @Builder
    public CreateOrderDto { // NOSONAR redudant constructor
        // We use constructor because of @Builder is not allowed on Record
    }
}
