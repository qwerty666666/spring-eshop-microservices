package com.example.eshop.sales.infrastructure.tests;

import com.example.eshop.sales.domain.order.Address;
import com.example.eshop.sales.domain.order.Delivery;
import com.example.eshop.sales.domain.order.Order;
import com.example.eshop.sales.domain.order.OrderLine;
import com.example.eshop.sales.domain.order.Payment;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FakeData {
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
                new OrderLine(Ean.fromString("2222222222222"),  2, Money.USD(123), productName(),
                        Collections.emptyList(), Collections.emptyList())
        );

        return new Order(id, customerId, delivery(), payment(), LocalDateTime.now(), orderLines);
    }
}
