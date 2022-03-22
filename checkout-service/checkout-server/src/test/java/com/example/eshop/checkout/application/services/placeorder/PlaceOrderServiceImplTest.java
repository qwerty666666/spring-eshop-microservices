package com.example.eshop.checkout.application.services.placeorder;

import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.checkout.application.services.OrderFactory;
import com.example.eshop.checkout.application.services.placeorder.orderplacedevent.OrderPlacedEventPublisher;
import com.example.eshop.checkout.application.services.placeorder.stockreservation.StockReservationService;
import com.example.eshop.checkout.domain.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.checkout.domain.payment.PaymentService.PaymentServiceId;
import com.example.eshop.checkout.domain.placeorder.PlaceOrderResult;
import com.example.eshop.checkout.domain.placeorder.PlaceOrderService;
import com.example.eshop.checkout.domain.placeorder.PlaceOrderValidator;
import com.example.eshop.checkout.infrastructure.tests.FakeData;
import com.example.eshop.checkout.stubs.DeliveryServiceStub;
import com.example.eshop.checkout.stubs.PaymentServiceStub;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.example.eshop.warehouse.client.reservationresult.InsufficientQuantityError;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaceOrderServiceImplTest {
    private final LocalDateTime creationDate = LocalDateTime.of(2021, Month.APRIL, 10, 3, 34, 10);
    private com.example.eshop.checkout.domain.placeorder.PlaceOrderService domainService;
    private OrderPlacedEventPublisher orderPlacedEventPublisher;
    private StockReservationService stockReservationService;

    private PlaceOrderServiceImpl placeOrderService;

    private CreateOrderDto createOrderDto;
    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        var customerId = FakeData.customerId();
        var cartDto = FakeData.cartDto();

        order = new Order(UUID.randomUUID(), customerId, cartDto, FakeData.deliveryAddress(),
                new DeliveryServiceStub(true), new PaymentServiceStub(true));
        createOrderDto = new CreateOrderDto(customerId, cartDto, FakeData.deliveryAddress(),
                new DeliveryServiceId("1"), new PaymentServiceId("1"));

        // domainService
        domainService = mock(PlaceOrderService.class);
        when(domainService.place(order))
                .thenReturn(PlaceOrderResult.success(order));

        // orderFactory
        OrderFactory orderFactory = mock(OrderFactory.class);
        when(orderFactory.create(createOrderDto))
                .thenReturn(order);

        // orderMapper
        OrderMapper orderMapper = new OrderMapperImpl();
        orderDto = orderMapper.toOrderDto(order);

        // orderPlacedEventPublisher
        orderPlacedEventPublisher = mock(OrderPlacedEventPublisher.class);

        // reserveStockItemService
        stockReservationService = mock(StockReservationService.class);
        when(stockReservationService.reserve(orderDto))
                .thenReturn(ReservationResult.success());

        // clock
        Clock clock = Clock.fixed(creationDate.atZone(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));

        // placeOrderService
        placeOrderService = new PlaceOrderServiceImpl(domainService, orderFactory, orderMapper,
                orderPlacedEventPublisher, stockReservationService, clock);
    }

    @Test
    void whenPlaceOrder_thenStocksReservedAndOrderCreatedEventIsPublished() {
        // Given
        var expectedEvent = new OrderPlacedEvent(orderDto, creationDate);

        // When
        placeOrderService.place(createOrderDto);

        // Then
        verify(domainService).place(order);
        verify(stockReservationService).reserve(orderDto);
        verify(orderPlacedEventPublisher).publish(expectedEvent);
    }

    @Test
    void givenInvalidCreateOrderDto_whenPlaceOrder_thenValidationExceptionIsThrownAndNoEventIsPublished() {
        // Given
        var errors = new Errors().addError("field", "message");
        when(domainService.place(order))
                .thenReturn(PlaceOrderResult.failure(order, errors));

        // When
        var exception = catchThrowableOfType(() -> placeOrderService.place(createOrderDto),
                ValidationException.class);

        // Then
        verify(domainService).place(order);
        assertThat(exception.getErrors()).isEqualTo(errors);
        verify(stockReservationService, never()).reserve(any());
        verify(orderPlacedEventPublisher, never()).publish(any());
    }

    @Test
    void givenExceededCartItemQuantity_whenPlaceOrder_thenValidationExceptionIsThrown() {
        // Given
        var cartItem = order.getCart().getItems().get(0);
        var error = new InsufficientQuantityError(cartItem.getEan(), cartItem.getQuantity(), 0, "");

        when(stockReservationService.reserve(orderDto))
                .thenReturn(ReservationResult.failure(List.of(error)));

        // When
        var exception = catchThrowableOfType(() -> placeOrderService.place(createOrderDto),
                ValidationException.class);

        // Then
        assertThat(exception.getErrors().getErrors(PlaceOrderValidator.CART_ITEMS_FIELD)).isNotEmpty();
        verify(stockReservationService).reserve(orderDto);
        verify(orderPlacedEventPublisher, never()).publish(any());
    }
}
