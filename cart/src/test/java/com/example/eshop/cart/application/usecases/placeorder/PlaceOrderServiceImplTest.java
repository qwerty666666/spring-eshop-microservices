package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.cart.stubs.DeliveryServiceStub;
import com.example.eshop.cart.stubs.PaymentServiceStub;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaceOrderServiceImplTest {
    private final static LocalDateTime CREATION_DATE = LocalDateTime.of(2021, Month.APRIL, 10, 3, 34, 10);

    private Clock clock;
    private CreateOrderDto createOrderDto;
    private Order order;
    private OrderFactory orderFactory;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(CREATION_DATE.atZone(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));

        createOrderDto = new CreateOrderDto(FakeData.customerId(), FakeData.cart(), FakeData.deliveryAddress(),
                new DeliveryServiceId("1"), new PaymentServiceId("1"));

        order = new Order(
                UUID.randomUUID(),
                FakeData.customerId(),
                FakeData.emptyCart(),
                FakeData.deliveryAddress(),
                new DeliveryServiceStub(true),
                new PaymentServiceStub(true)
        );

        orderFactory = mock(OrderFactory.class);
        when(orderFactory.create(createOrderDto)).thenReturn(order);

        eventPublisher = mock(ApplicationEventPublisher.class);
    }

    @Test
    void whenPlaceOrder_thenOrderCreatedEventIsPublished() {
        // Given
        var domainService = mock(PlaceOrderService.class);
        var service = new PlaceOrderServiceImpl(domainService, eventPublisher, orderFactory, clock);

        var expectedEvent = new OrderPlacedEvent(order, CREATION_DATE);

        // When
        service.place(createOrderDto);

        // Then
        verify(domainService).place(order);
        verify(eventPublisher).publishEvent(expectedEvent);
    }

    @Test
    void givenInvalidCreateOrderDto_whenPlace_thenNoEventIsPublished() {
        // Given
        var domainService = mock(PlaceOrderService.class);
        doThrow(new ValidationException(new Errors())).when(domainService).place(order);

        var service = new PlaceOrderServiceImpl(domainService, eventPublisher, orderFactory, clock);

        // When
        //noinspection ResultOfMethodCallIgnored
        catchThrowableOfType(() -> service.place(createOrderDto), ValidationException.class);

        // Then
        verify(domainService).place(order);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
