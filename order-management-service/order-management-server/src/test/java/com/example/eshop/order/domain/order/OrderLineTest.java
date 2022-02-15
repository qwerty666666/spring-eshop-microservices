package com.example.eshop.order.domain.order;

import com.example.eshop.order.infrastructure.tests.FakeData;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.Test;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class OrderLineTest {
    @Test
    void whenGetPrice_thenReturnItemPriceMultipliedByQuantity() {
        var orderLine = new OrderLine(FakeData.ean(), 3, Money.USD(4), "Sneakers",
                Collections.emptyList(), Collections.emptyList());

        assertThat(orderLine.getPrice()).isEqualTo(Money.USD(12));
    }
}
