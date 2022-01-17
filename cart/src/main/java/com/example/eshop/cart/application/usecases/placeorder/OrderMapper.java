package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.checkout.client.order.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.Nullable;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "paymentServiceId", source = "deliveryService.id")
    @Mapping(target = "deliveryServiceId", source = "paymentService.id")
    @Mapping(target = "totalPrice", source = "totalPrice")
    OrderDto toOrderDto(Order order);

    @Nullable
    default String toString(@Nullable DeliveryServiceId id) {
        return id == null ? null : id.toString();
    }

    @Nullable
    default String toString(@Nullable PaymentServiceId id) {
        return id == null ? null : id.toString();
    }
}
