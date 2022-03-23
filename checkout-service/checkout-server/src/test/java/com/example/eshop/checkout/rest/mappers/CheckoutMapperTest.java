package com.example.eshop.checkout.rest.mappers;

import com.example.eshop.checkout.FakeData;
import com.example.eshop.checkout.application.services.checkoutprocess.dto.CheckoutForm;
import com.example.eshop.checkout.application.services.checkoutprocess.dto.Total;
import com.example.eshop.checkout.client.model.CheckoutFormDto;
import com.example.eshop.checkout.client.model.CheckoutTotalDto;
import com.example.eshop.checkout.client.model.DeliveryAddressDto;
import com.example.eshop.checkout.client.model.DeliveryServiceDto;
import com.example.eshop.checkout.client.model.PaymentServiceDto;
import com.example.eshop.checkout.config.MappersTest;
import com.example.eshop.checkout.domain.delivery.DeliveryService;
import com.example.eshop.checkout.domain.order.DeliveryAddress;
import com.example.eshop.checkout.domain.payment.PaymentService;
import com.example.eshop.sharedtest.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@MappersTest
class CheckoutMapperTest {
    @Autowired
    private CheckoutMapper checkoutMapper;

    @Test
    void toCheckoutFormDtoTest() {
        // Given
        var checkoutForm = FakeData.checkoutForm();

        // When
        var dto = checkoutMapper.toCheckoutFormDto(checkoutForm);

        // Then
        assertCheckoutFormEquals(checkoutForm, dto);
    }

    private void assertCheckoutFormEquals(CheckoutForm form, CheckoutFormDto dto) {
        // cart
        assertThat(dto.getCart()).isEqualTo(form.order().getCart());
        // address
        assertAddressEquals(form.order().getAddress(), dto.getDeliveryAddress());
        // deliveries
        Assertions.assertListEquals(form.availableDeliveries(), dto.getAvailableDeliveries(), this::assertDeliveryServiceEquals);
        // payments
        Assertions.assertListEquals(form.availablePayments(), dto.getAvailablePayments(), this::assertPaymentServiceEquals);
        // Total
        assertTotalEquals(form.total(), dto.getTotal());
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
        assertThat(dto.getCartPrice()).isEqualTo(total.cartPrice());
        assertThat(dto.getDeliveryPrice()).isEqualTo(total.deliveryPrice());
        assertThat(dto.getTotalPrice()).isEqualTo(total.totalPrice());
    }
}
