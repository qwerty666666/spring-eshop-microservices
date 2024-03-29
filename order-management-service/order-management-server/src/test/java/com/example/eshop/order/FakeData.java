package com.example.eshop.order;

import com.example.eshop.cart.client.model.AttributeDto;
import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.cart.client.model.CartItemDto;
import com.example.eshop.cart.client.model.ImageDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryAddressDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryServiceDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentDto;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentServiceDto;
import com.example.eshop.order.domain.order.Address;
import com.example.eshop.order.domain.order.Delivery;
import com.example.eshop.order.domain.order.Order;
import com.example.eshop.order.domain.order.OrderLine;
import com.example.eshop.order.domain.order.Payment;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FakeData {
    private static final RandomGenerator random = RandomGenerator.getDefault();

    public static OrderDto orderDto() {
        var skuPrice = Money.USD(10);
        var quantity = 2;
        var cartPrice = Money.USD(20);
        var deliveryPrice = Money.USD(12);
        var totalPrice = Money.USD(32);

        return OrderDto.builder()
                .id(UUID.randomUUID())
                .customerId("customerId")
                .cart(new CartDto()
                        .id("1")
                        .totalPrice(cartPrice)
                        .items(List.of(
                                new CartItemDto()
                                        .ean(ean())
                                        .price(skuPrice)
                                        .quantity(quantity)
                                        .availableQuantity(quantity)
                                        .productName("productName")
                                        .attributes(List.of(
                                                new AttributeDto("1", "size", "42")
                                        ))
                                        .images(List.of(
                                                new ImageDto("url")
                                        ))
                        ))
                )
                .totalPrice(totalPrice)
                .delivery(new DeliveryDto(
                        new DeliveryAddressDto(fullname(), phone(), country(), city(), street(), building(), flat()),
                        new DeliveryServiceDto("deliveryServiceId", "deliveryServiceName"),
                        deliveryPrice
                ))
                .payment(new PaymentDto(
                        new PaymentServiceDto("paymentServiceId", "paymentServiceName")
                ))
                .build();
    }

    public static Ean ean() {
        return Ean.fromString("1234567890123");
    }

    public static String customerId() {
        return "1";
    }

    public static String productName() {
        return "Sneakers";
    }

    public static Payment payment() {
        return new Payment("1", "cash");
    }

    public static Delivery delivery() {
        return delivery(Money.USD(4));
    }

    public static Delivery delivery(Money price) {
        return Delivery.builder()
                .deliveryServiceId("1")
                .name("courier")
                .price(price)
                .address(address())
                .build();
    }

    public static Address address() {
        return new Address(fullname(), phone(), country(), city(), street(), building(), flat());
    }

    public static String fullname() {
        return "fullname";
    }

    public static String country() {
        return "country";
    }

    public static String city() {
        return "city";
    }

    public static String building() {
        return "building";
    }

    public static String street() {
        return "street";
    }

    public static String flat() {
        return "flat";
    }

    public static Phone phone() {
        return Phone.fromString("+79993334444");
    }

    public static Order order() {
        return order(customerId());
    }

    public static Order order(String customerId) {
        return order(UUID.randomUUID(), customerId);
    }

    public static Order order(UUID id, String customerId) {
        var orderLines = List.of(
                new OrderLine(Ean.fromString("1111111111111"), 1, Money.USD(12), productName(),
                        Collections.emptyList(), Collections.emptyList()),
                new OrderLine(Ean.fromString("2222222222222"), 2, Money.USD(123), productName(),
                        Collections.emptyList(), Collections.emptyList())
        );

        return new Order(id, customerId, delivery(), payment(), LocalDateTime.now(), orderLines);
    }
}
