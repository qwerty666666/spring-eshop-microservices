package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.cart.stubs.DeliveryServiceStub;
import com.example.eshop.cart.stubs.PaymentServiceStub;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaceOrderServiceTest {
    private Order order;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        order = new Order(
                UUID.randomUUID(),
                FakeData.customerId(),
                FakeData.emptyCart(),
                FakeData.deliveryAddress(),
                new DeliveryServiceStub(true),
                new PaymentServiceStub(true)
        );

        eventPublisher = mock(ApplicationEventPublisher.class);
    }

    @Test
    void whenPlaceInvalidOrder_thenThrowValidationException() {
        // Given
        var errors = new Errors().addError("field", "message");
        var validator = mock(PlaceOrderValidator.class);
        when(validator.validate(order)).thenReturn(errors);

        var service = new PlaceOrderService(validator);

        // When + Then
        var exception = catchThrowableOfType(() -> service.place(order), ValidationException.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrors()).isSameAs(errors);

        verify(validator).validate(order);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
