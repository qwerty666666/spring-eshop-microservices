package com.example.eshop.checkout.domain.placeorder;

import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.checkout.infrastructure.tests.FakeData;
import com.example.eshop.checkout.stubs.DeliveryServiceStub;
import com.example.eshop.checkout.stubs.PaymentServiceStub;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaceOrderServiceTest {
    private final Order order = new Order(
            UUID.randomUUID(),
            FakeData.customerId(),
            FakeData.emptyCartDto(),
            FakeData.deliveryAddress(),
            new DeliveryServiceStub(true),
            new PaymentServiceStub(true)
    );

    @Test
    void whenPlaceValidOrder_thenReturnSuccessResult() {
        // Given
        var validator = mock(PlaceOrderValidator.class);
        when(validator.validate(order)).thenReturn(new Errors());

        var service = new PlaceOrderService(validator);

        // When
        var result = service.place(order);

        // Then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void whenPlaceInvalidOrder_thenReturnFailureResult() {
        // Given
        var errors = new Errors().addError("field", "message");
        var validator = mock(PlaceOrderValidator.class);
        when(validator.validate(order)).thenReturn(errors);

        var service = new PlaceOrderService(validator);

        // When
        var result = service.place(order);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrors()).isSameAs(errors);

        verify(validator).validate(order);
    }
}
