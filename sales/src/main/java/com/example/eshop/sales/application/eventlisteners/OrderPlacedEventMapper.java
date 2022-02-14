package com.example.eshop.sales.application.eventlisteners;

import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.catalog.client.api.model.AttributeDto;
import com.example.eshop.catalog.client.api.model.ImageDto;
import com.example.eshop.catalog.client.api.model.MoneyDto;
import com.example.eshop.checkout.client.events.orderplacedevent.CartItemDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryAddressDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryDto;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentDto;
import com.example.eshop.sales.domain.Address;
import com.example.eshop.sales.domain.Delivery;
import com.example.eshop.sales.domain.Order;
import com.example.eshop.sales.domain.OrderLine;
import com.example.eshop.sales.domain.OrderLineAttribute;
import com.example.eshop.sales.domain.Payment;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderPlacedEventMapper {
    default Order toOrder(OrderPlacedEvent event) {
        var order = event.order();

        var orderLines = order.cart().items().stream()
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

    @Mapping(target = "itemPrice", source = "sku.price")
    @Mapping(target = "productName", source = "sku.product.name")
    @Mapping(target = "attributes", source = "sku.attributes")
    @Mapping(target = "images", source = "sku.product.images")
    OrderLine toOrderLine(CartItemDto item);

    default String toImage(ImageDto image) {
        return image.getUrl();
    }

    default OrderLineAttribute toOrderLineAttribute(AttributeDto attr) {
        return new OrderLineAttribute(attr.getId(), attr.getValue(), attr.getName());
    }

    default Money toMoney(MoneyDto money) {
        return Money.of(money.getAmount(), money.getCurrency());
    }
}
