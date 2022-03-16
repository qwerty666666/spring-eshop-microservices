package com.example.eshop.order.application.eventlisteners;

import com.example.eshop.catalog.client.model.AttributeDto;
import com.example.eshop.catalog.client.model.ImageDto;
import com.example.eshop.checkout.client.events.orderplacedevent.CartItemDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryAddressDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentDto;
import com.example.eshop.order.FakeData;
import com.example.eshop.order.domain.order.Address;
import com.example.eshop.order.domain.order.Delivery;
import com.example.eshop.order.domain.order.Order;
import com.example.eshop.order.domain.order.OrderLine;
import com.example.eshop.order.domain.order.OrderLineAttribute;
import com.example.eshop.order.domain.order.Payment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OrderPlacedEventMapperImplTest {
    @Test
    void testToOrder() {
        var orderDto = FakeData.orderDto();
        var orderPlacedEvent = new OrderPlacedEvent(orderDto, LocalDateTime.now());
        var mapper = new OrderPlacedEventMapperImpl();

        var order = mapper.toOrder(orderPlacedEvent);

        assertOrderEquals(order, orderPlacedEvent);
    }

    private void assertOrderEquals(Order order, OrderPlacedEvent event) {
        var orderDto = event.order();

        assertThat(order.getId()).isEqualTo(orderDto.id());
        assertThat(order.getCustomerId()).isEqualTo(orderDto.customerId());
        assertThat(order.getCreationDate()).isEqualTo(event.creationDate());
        assertThat(order.getCartPrice()).isEqualTo(orderDto.cart().price());
        assertDeliveryEquals(order.getDelivery(), orderDto.delivery());
        assertPaymentEquals(order.getPayment(), orderDto.payment());
        assertListEquals(order.getLines(), orderDto.cart().items(), this::assertOrderLineEquals);
    }

    private void assertDeliveryEquals(Delivery delivery, DeliveryDto deliveryDto) {
        assertThat(delivery.getDeliveryServiceId()).isEqualTo(deliveryDto.deliveryService().id());
        assertThat(delivery.getName()).isEqualTo(deliveryDto.deliveryService().name());
        assertThat(delivery.getPrice()).isEqualTo(deliveryDto.price());
        assertAddressEquals(delivery.getAddress(), deliveryDto.address());
    }

    private void assertAddressEquals(Address address, DeliveryAddressDto addressDto) {
        assertThat(address.getCountry()).isEqualTo(addressDto.country());
        assertThat(address.getCity()).isEqualTo(addressDto.city());
        assertThat(address.getStreet()).isEqualTo(addressDto.street());
        assertThat(address.getBuilding()).isEqualTo(addressDto.building());
        assertThat(address.getFlat()).isEqualTo(addressDto.flat());
        assertThat(address.getFullname()).isEqualTo(addressDto.fullname());
        assertThat(address.getPhone()).isEqualTo(addressDto.phone());
    }

    private void assertPaymentEquals(Payment payment, PaymentDto paymentDto) {
        assertThat(payment.getPaymentServiceId()).isEqualTo(paymentDto.paymentService().id());
        assertThat(payment.getName()).isEqualTo(paymentDto.paymentService().name());
    }

    private void assertOrderLineEquals(OrderLine line, CartItemDto itemDto) {
        assertThat(line.getProductName()).isEqualTo(itemDto.sku().getProduct().getName());
        assertThat(line.getQuantity()).isEqualTo(itemDto.quantity());
        assertThat(line.getPrice()).isEqualTo(itemDto.price());
        assertThat(line.getEan()).isEqualTo(itemDto.ean());
        assertThat(line.getItemPrice()).isEqualTo(itemDto.sku().getPrice());
        assertListEquals(line.getAttributes(), itemDto.sku().getAttributes(), this::assertAttributeEquals);
        assertListEquals(line.getImages(), itemDto.sku().getProduct().getImages(), this::assertImageEquals);
    }

    private void assertAttributeEquals(OrderLineAttribute attr, AttributeDto attrDto) {
        assertThat(attr.getAttributeId()).isEqualTo(attrDto.getId());
        assertThat(attr.getName()).isEqualTo(attrDto.getName());
        assertThat(attr.getValue()).isEqualTo(attrDto.getValue());
    }

    private void assertImageEquals(String image, ImageDto imageDto) {
        assertThat(image).isEqualTo(imageDto.getUrl());
    }

    private <T1, T2> void assertListEquals(List<T1> list1, List<T2> list2, BiConsumer<T1, T2> itemAssertion) {
        Assertions.assertThat(list1).hasSize(list2.size());

        for (int i = 0; i < list1.size(); i++) {
            itemAssertion.accept(list1.get(i), list2.get(i));
        }
    }
}
