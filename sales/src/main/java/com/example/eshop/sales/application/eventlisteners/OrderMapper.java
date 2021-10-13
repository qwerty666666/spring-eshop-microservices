package com.example.eshop.sales.application.eventlisteners;

import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.sales.domain.Address;
import com.example.eshop.sales.domain.Delivery;
import com.example.eshop.sales.domain.OrderLine;
import com.example.eshop.sales.domain.Payment;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    default com.example.eshop.sales.domain.Order toOrder(Order order) {
        return new com.example.eshop.sales.domain.Order(
                order.getId(),
                order.getCustomerId(),
                toDelivery(order),
                toPayment(order.getPaymentService()),
                order.getCart().getItems().stream().map(this::toOrderLine).toList()
        );
    }

    default Delivery toDelivery(Order order) {
        var deliveryService = order.getDeliveryService();

        if (deliveryService == null) {
            return null;
        }

        return Delivery.builder()
                .id(deliveryService.getId() == null ? null : deliveryService.getId().toString())
                .name(order.getDeliveryService().getName())
                .price(order.getShipmentInfo() == null ? Money.ZERO : order.getShipmentInfo().price())
                .address(toAddress(order.getAddress()))
                .build();
    }

    default Payment toPayment(PaymentService payment) {
        if (payment == null) {
            return null;
        }

        return new Payment(payment.getId() == null ? null : payment.getId().toString(), payment.getName());
    }

    Address toAddress(DeliveryAddress address);

    @Mapping(target = "itemPrice", source = "price")
    OrderLine toOrderLine(CartItem item);
}
