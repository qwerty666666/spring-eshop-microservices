package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.placeorder.OrderPlacedEvent;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderValidator;
import com.example.eshop.cart.stubs.DeliveryServiceStub;
import com.example.eshop.cart.stubs.PaymentServiceStub;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaceOrderServiceTest {
    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order("1", new Cart("1"), new DeliveryAddress(), new DeliveryServiceStub(true),
                new PaymentServiceStub(true), DeliveryServiceStub.COST);
    }

    @Test
    void whenPlaceOrder_thenOrderCreatedEventIsPublished() {
        // Given
        var eventPublisher = mock(ApplicationEventPublisher.class);

        var validator = mock(PlaceOrderValidator.class);
        when(validator.validate(order)).thenReturn(new Errors());

        var service = new PlaceOrderService(eventPublisher, validator);

        var expectedEvent = new OrderPlacedEvent(order);

        // When
        service.place(order);

        // Then
        verify(validator).validate(order);
        verify(eventPublisher).publishEvent(eq(expectedEvent));
    }

    @Test
    void whenPlaceInvalidOrder_thenThrowValidationException() {
        // Given
        var eventPublisher = mock(ApplicationEventPublisher.class);

        var errors = new Errors();
        errors.addError("field", "message");

        var validator = mock(PlaceOrderValidator.class);
        when(validator.validate(order)).thenReturn(errors);

        var service = new PlaceOrderService(eventPublisher, validator);

        // When + Then
        var exception = catchThrowableOfType(() -> service.place(order), ValidationException.class);

        assertThat(exception).isNotNull();
        assertThat(exception.getErrors()).isSameAs(errors);

        verify(validator).validate(order);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
