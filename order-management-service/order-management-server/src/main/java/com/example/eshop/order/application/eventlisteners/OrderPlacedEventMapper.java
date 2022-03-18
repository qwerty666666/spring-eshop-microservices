package com.example.eshop.order.application.eventlisteners;

import com.example.eshop.cart.client.model.AttributeDto;
import com.example.eshop.cart.client.model.CartItemDto;
import com.example.eshop.cart.client.model.ImageDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryAddressDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentDto;
import com.example.eshop.order.domain.order.Address;
import com.example.eshop.order.domain.order.Delivery;
import com.example.eshop.order.domain.order.Order;
import com.example.eshop.order.domain.order.OrderLine;
import com.example.eshop.order.domain.order.OrderLineAttribute;
import com.example.eshop.order.domain.order.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderPlacedEventMapper {
    default Order toOrder(OrderPlacedEvent event) {
        var order = event.order();

        var orderLines = order.cart().getItems().stream()
                .map(this::toOrderLine)
                .toList();

        return new Order(
                order.id(),
                order.customerId(),
                toDelivery(order.delivery()),
                toPayment(order.payment()),
                event.creationDate(),
                orderLines
        );
    }

    @Mapping(target = "deliveryServiceId", source = "deliveryService.id")
    @Mapping(target = "name", source = "deliveryService.name")
    Delivery toDelivery(DeliveryDto delivery);

    @Mapping(target = "paymentServiceId", source = "paymentService.id")
    @Mapping(target = "name", source = "paymentService.name")
    Payment toPayment(PaymentDto payment);

    Address toAddress(DeliveryAddressDto address);

    @Mapping(target = "itemPrice", source = "price")
    OrderLine toOrderLine(CartItemDto item);

    default String toImage(ImageDto image) {
        return image.getUrl();
    }

    default OrderLineAttribute toOrderLineAttribute(AttributeDto attr) {
        return new OrderLineAttribute(Long.valueOf(attr.getId()), attr.getValue(), attr.getName());
    }
}
