package com.example.eshop.rest.mappers;

import com.example.eshop.rest.config.MappersConfig;
import com.example.eshop.rest.dto.DeliveryAddressDto;
import com.example.eshop.rest.dto.OrderDeliveryDto;
import com.example.eshop.rest.dto.OrderDto;
import com.example.eshop.rest.dto.OrderLineDto;
import com.example.eshop.rest.dto.OrderPaymentDto;
import com.example.eshop.rest.dto.OrderTotalDto;
import com.example.eshop.sales.domain.Address;
import com.example.eshop.sales.domain.Delivery;
import com.example.eshop.sales.domain.Order;
import com.example.eshop.sales.domain.OrderLine;
import com.example.eshop.sales.domain.Payment;
import com.example.eshop.sales.infrastructure.tests.FakeData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MappersConfig.class)
class OrderPlacedEventMapperTest {
    @Autowired
    OrderMapper orderMapper;

    @Test
    void testToPagedOrderListDto() {
        // Given
        var page = new PageImpl<>(List.of(FakeData.order()), PageRequest.of(1, 1), 4);

        // When
        var dto = orderMapper.toPagedOrderListDto(page);

        // Then
        Assertions.assertPageableEquals(page, dto.getPageable());
        Assertions.assertListEquals(page.stream().toList(), dto.getItems(), this::assertOrderEquals);
    }

    private void assertOrderEquals(Order order, OrderDto dto) {
        assertThat(dto.getId()).as("ID").isEqualTo(order.getId());
        assertAddressEquals(order.getDelivery().getAddress(), dto.getAddress());
        assertDeliveryEquals(order.getDelivery(), dto.getDelivery());
        assertPaymentEquals(order.getPayment(), dto.getPayment());
        assertTotalEquals(order, dto.getTotal());
        Assertions.assertListEquals(order.getLines(), dto.getLines(), this::assertOrderLineEquals);
    }

    private void assertAddressEquals(Address address, DeliveryAddressDto dto) {
        assertThat(dto.getCountry()).isEqualTo(address.getCountry());
        assertThat(dto.getCity()).isEqualTo(address.getCity());
        assertThat(dto.getStreet()).isEqualTo(address.getStreet());
        assertThat(dto.getBuilding()).isEqualTo(address.getBuilding());
        assertThat(dto.getFlat()).isEqualTo(address.getFlat());
        assertThat(dto.getFullname()).isEqualTo(address.getFullname());
        assertThat(dto.getPhone()).isEqualTo(address.getPhone() == null ? null : address.getPhone().toString());
    }

    private void assertDeliveryEquals(Delivery delivery, OrderDeliveryDto dto) {
        assertThat(dto.getName()).isEqualTo(delivery.getName());
    }

    private void assertPaymentEquals(Payment payment, OrderPaymentDto dto) {
        assertThat(dto.getName()).isEqualTo(payment.getName());
    }

    private void assertTotalEquals(Order order, OrderTotalDto dto) {
        Assertions.assertPriceEquals(order.getCartPrice(), dto.getCartPrice());
        Assertions.assertPriceEquals(order.getDelivery().getPrice(), dto.getDeliveryPrice());
        Assertions.assertPriceEquals(order.getPrice(), dto.getTotalPrice());
    }

    private void assertOrderLineEquals(OrderLine line, OrderLineDto dto) {
        // id
        assertThat(dto.getId()).isEqualTo(line.getId() == null ? null : line.getId().toString());
        // ean
        assertThat(dto.getEan()).isEqualTo(line.getEan().toString());
        // product name
        assertThat(dto.getProductName()).isEqualTo(line.getProductName());
        // quantity
        assertThat(dto.getQuantity()).isEqualTo(line.getQuantity());
        // item price
        Assertions.assertPriceEquals(line.getItemPrice(), dto.getItemPrice());
        // line price
        Assertions.assertPriceEquals(line.getPrice(), dto.getLinePrice());
    }
}