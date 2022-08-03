package com.example.eshop.order.rest.mappers;

import com.example.eshop.order.FakeData;
import com.example.eshop.order.client.model.AttributeDto;
import com.example.eshop.order.client.model.DeliveryAddressDto;
import com.example.eshop.order.client.model.ImageDto;
import com.example.eshop.order.client.model.OrderDeliveryDto;
import com.example.eshop.order.client.model.OrderDto;
import com.example.eshop.order.client.model.OrderLineDto;
import com.example.eshop.order.client.model.OrderPaymentDto;
import com.example.eshop.order.client.model.OrderTotalDto;
import com.example.eshop.order.client.model.PageableDto;
import com.example.eshop.order.config.MapperTest;
import com.example.eshop.order.domain.order.Address;
import com.example.eshop.order.domain.order.Delivery;
import com.example.eshop.order.domain.order.Order;
import com.example.eshop.order.domain.order.OrderLine;
import com.example.eshop.order.domain.order.OrderLineAttribute;
import com.example.eshop.order.domain.order.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

@MapperTest
class OrderMapperImplTest {
    @Autowired
    OrderMapper orderMapper;

    @Test
    void assertToOrderDto() {
        // Given
        var order = FakeData.order();

        // When
        var dto = orderMapper.toOrderDto(order);

        // Then
        assertOrderEquals(order, dto);
    }

    @Test
    void testToPagedOrderListDto() {
        // Given
        var page = new PageImpl<>(List.of(FakeData.order()), PageRequest.of(1, 1), 4);

        // When
        var dto = orderMapper.toPagedOrderListDto(page);

        // Then
        assertPageableEquals(page, dto.getPageable());
        assertListEquals(page.getContent(), dto.getItems(), this::assertOrderEquals);
    }

    private void assertPageableEquals(Page<?> page, PageableDto pageableDto) {
        assertThat(pageableDto.getPage()).isEqualTo(page.getNumber() + 1);
        assertThat(pageableDto.getPerPage()).isEqualTo(page.getSize());
        assertThat(pageableDto.getTotalPages()).isEqualTo(page.getTotalPages());
        assertThat(pageableDto.getTotalItems()).isEqualTo((int)page.getTotalElements());
    }

    private void assertOrderEquals(Order order, OrderDto dto) {
        assertThat(dto.getId()).isEqualTo(order.getId());
        assertAddressEquals(order.getDelivery().getAddress(), dto.getAddress());
        assertDeliveryEquals(order.getDelivery(), dto.getDelivery());
        assertPaymentEquals(order.getPayment(), dto.getPayment());
        assertTotalEquals(order, dto.getTotal());
        assertListEquals(order.getLines(), dto.getLines(), this::assertOrderLineEquals);
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
        assertThat(dto.getCartPrice()).isEqualTo(order.getCartPrice());
        assertThat(dto.getDeliveryPrice()).isEqualTo(order.getDelivery().getPrice());
        assertThat(dto.getTotalPrice()).isEqualTo(order.getPrice());
    }

    private void assertOrderLineEquals(OrderLine line, OrderLineDto dto) {
        assertThat(dto.getId()).isEqualTo(line.getId() == null ? null : line.getId().toString());
        assertThat(dto.getEan()).isEqualTo(line.getEan());
        assertThat(dto.getProductName()).isEqualTo(line.getProductName());
        assertThat(dto.getQuantity()).isEqualTo(line.getQuantity());
        assertThat(dto.getItemPrice()).isEqualTo(line.getItemPrice());
        assertThat(dto.getLinePrice()).isEqualTo(line.getPrice());
        assertListEquals(line.getAttributes(), dto.getAttributes(), this::assertAttributeEquals);
        assertListEquals(line.getImages(), dto.getImages(), this::assertImageEquals);
    }

    private <T1, T2> void assertListEquals(List<T1> list1, List<T2> list2, BiConsumer<T1, T2> itemAssertion) {
        assertThat(list1).hasSize(list2.size());

        for (int i = 0; i < list1.size(); i++) {
            itemAssertion.accept(list1.get(i), list2.get(i));
        }
    }

    private void assertAttributeEquals(OrderLineAttribute attr, AttributeDto dto) {
        assertThat(dto.getId()).isEqualTo(Optional.ofNullable(attr).map(OrderLineAttribute::getAttributeId).orElse(null));
        assertThat(dto.getName()).isEqualTo(attr.getName());
        assertThat(dto.getValue()).isEqualTo(attr.getValue());
    }

    private void assertImageEquals(String image, ImageDto dto) {
        assertThat(dto.getUrl()).isEqualTo(image);
    }
}
