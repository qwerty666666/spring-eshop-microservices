package com.example.eshop.rest.mappers;

import com.example.eshop.cart.application.usecases.checkout.CheckoutForm;
import com.example.eshop.cart.application.usecases.checkout.Total;
import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentInfo;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.rest.config.MappersTest;
import com.example.eshop.rest.dto.CheckoutFormDto;
import com.example.eshop.rest.dto.CheckoutTotalDto;
import com.example.eshop.rest.dto.DeliveryAddressDto;
import com.example.eshop.rest.dto.DeliveryServiceDto;
import com.example.eshop.rest.dto.PaymentServiceDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@MappersTest
class CheckoutMapperTest {
    @Autowired
    private CheckoutMapper checkoutMapper;

    @Test
    void toCheckoutFormDtoTest() {
        // Given
        var deliveryService = new DeliveryServiceStub(new DeliveryServiceId("1"), "delivery");
        var paymentService = new PaymentServiceStub(new PaymentServiceId("1"), "payment");
        var cartDto = FakeData.emptyCartDto();

        var checkoutForm = CheckoutForm.builder()
                .order(new Order(UUID.randomUUID(), FakeData.customerId(), cartDto, FakeData.deliveryAddress(), deliveryService, paymentService))
                .availableDeliveries(List.of(deliveryService))
                .availablePayments(List.of(paymentService))
                .build();

        // When
        var dto = checkoutMapper.toCheckoutFormDto(checkoutForm);

        // Then
        assertCheckoutFormEquals(checkoutForm, dto, cartDto);
    }

    private void assertCheckoutFormEquals(CheckoutForm form, CheckoutFormDto dto, CartDto expectedCartDto) {
        // cart
        assertThat(dto.getCart()).isEqualTo(expectedCartDto);
        // address
        assertAddressEquals(form.getOrder().getAddress(), dto.getDeliveryAddress());
        // deliveries
        Assertions.assertListEquals(form.getAvailableDeliveries(), dto.getAvailableDeliveries(), this::assertDeliveryServiceEquals);
        // payments
        Assertions.assertListEquals(form.getAvailablePayments(), dto.getAvailablePayments(), this::assertPaymentServiceEquals);
        // Total
        assertTotalEquals(form.getTotal(), dto.getTotal());
    }

    private void assertAddressEquals(DeliveryAddress address, DeliveryAddressDto dto) {
        assertThat(dto.getCountry()).isEqualTo(address.country());
        assertThat(dto.getCity()).isEqualTo(address.city());
        assertThat(dto.getStreet()).isEqualTo(address.street());
        assertThat(dto.getBuilding()).isEqualTo(address.building());
        assertThat(dto.getFlat()).isEqualTo(address.flat());
        assertThat(dto.getFullname()).isEqualTo(address.fullname());
        assertThat(dto.getPhone()).isEqualTo(address.phone() == null ? null : address.phone().toString());
    }

    private void assertDeliveryServiceEquals(DeliveryService service, DeliveryServiceDto dto) {
        assertThat(dto.getId()).isEqualTo(service.getId() == null ? null : service.getId().toString());
        assertThat(dto.getName()).isEqualTo(service.getName());
    }

    private void assertPaymentServiceEquals(PaymentService service, PaymentServiceDto dto) {
        assertThat(dto.getId()).isEqualTo(service.getId() == null ? null : service.getId().toString());
        assertThat(dto.getName()).isEqualTo(service.getName());
    }

    private void assertTotalEquals(Total total, CheckoutTotalDto dto) {
        assertThat(dto.getCartPrice()).isEqualTo(total.getCartPrice());
        assertThat(dto.getDeliveryPrice()).isEqualTo(total.getDeliveryPrice());
        assertThat(dto.getTotalPrice()).isEqualTo(total.getTotalPrice());
    }

    private static class DeliveryServiceStub extends DeliveryService {
        public DeliveryServiceStub(DeliveryServiceId id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public ShipmentInfo getShipmentInfo(Order order) {
            return null;
        }
    }

    private static class PaymentServiceStub extends PaymentService {
        public PaymentServiceStub(PaymentServiceId id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean canPay(Order order) {
            return true;
        }
    }
}
