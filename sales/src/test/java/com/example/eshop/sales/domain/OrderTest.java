package com.example.eshop.sales.domain;

import com.example.eshop.sales.infrastructure.tests.FakeData;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {
    private final static Money DELIVERY_PRICE = Money.USD(4);
    private final static Money ORDER_LINE_1_PRICE = Money.USD(5);
    private final static int ORDER_LINE_1_QUANTITY = 3;
    private final static Money ORDER_LINE_2_PRICE = Money.USD(10);
    private final static int ORDER_LINE_2_QUANTITY = 2;

    private Order order;

    @BeforeEach
    void setUp() {
        var orderLines = List.of(
                new OrderLine(Ean.fromString("1111111111111"), ORDER_LINE_1_QUANTITY, ORDER_LINE_1_PRICE,
                        FakeData.productName(), Collections.emptyList(), Collections.emptyList()),
                new OrderLine(Ean.fromString("2222222222222"),  ORDER_LINE_2_QUANTITY, ORDER_LINE_2_PRICE,
                        FakeData.productName(), Collections.emptyList(), Collections.emptyList())
        );

        order = new Order(
                UUID.randomUUID(),
                FakeData.customerId(),
                FakeData.delivery(DELIVERY_PRICE),
                FakeData.payment(),
                LocalDateTime.now(),
                orderLines
        );
    }

    @Test
    void whenGetCartPrice_thenReturnSumOfOrderLinePrices() {
        assertThat(order.getCartPrice()).isEqualTo(getCartPrice());
    }

    @Test
    void whenGetPrice_thenReturnSumOfCartPriceAndDeliveryPrice() {
        var expectedPrice = DELIVERY_PRICE.add(getCartPrice());

        assertThat(order.getPrice()).isEqualTo(expectedPrice);
    }

    private Money getCartPrice() {
        var orderLine1TotalPrice = ORDER_LINE_1_PRICE.multiply(ORDER_LINE_1_QUANTITY);
        var orderLine2TotalPrice = ORDER_LINE_2_PRICE.multiply(ORDER_LINE_2_QUANTITY);

        return orderLine1TotalPrice.add(orderLine2TotalPrice);
    }
}
