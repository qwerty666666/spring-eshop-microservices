package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.Shipment;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.catalog.client.model.SkuWithProductDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryAddressDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryServiceDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentDto;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentServiceDto;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.mapstruct.Mapper;
import org.springframework.lang.Nullable;
import java.util.Map;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    default OrderDto toOrderDto(Order order, Map<Ean, SkuWithProductDto> skuMap) {
        return new OrderDto(
                order.getId(),
                order.getCustomerId(),
                order.getCart(),
                order.getTotalPrice(),
                toDeliveryDto(order),
                toPaymentDto(order)
        );
    }

    private DeliveryDto toDeliveryDto(Order order) {
        if (order.getShipment().equals(Shipment.nullShipment())) {
            throw new IllegalArgumentException("Shipment is not provided for Order. Shipment is required for OrderDto");
        }
        var deliveryPrice = order.getShipment().price();

        return new DeliveryDto(
                toDeliveryAddressDto(order.getAddress()),
                toDeliveryServiceDto(order.getDeliveryService()),
                deliveryPrice
        );
    }

    DeliveryAddressDto toDeliveryAddressDto(DeliveryAddress deliveryAddress);

    @Nullable
    DeliveryServiceDto toDeliveryServiceDto(@Nullable DeliveryService deliveryService);

    @Nullable
    default String toString(@Nullable DeliveryServiceId id) {
        return Optional.ofNullable(id).map(DomainObjectId::toString).orElse(null);
    }

    private PaymentDto toPaymentDto(Order order) {
        return new PaymentDto(toPaymentServiceDto(order.getPaymentService()));
    }

    @Nullable
    PaymentServiceDto toPaymentServiceDto(@Nullable PaymentService deliveryService);

    @Nullable
    default String toString(@Nullable PaymentServiceId id) {
        return Optional.ofNullable(id).map(DomainObjectId::toString).orElse(null);
    }
}
