package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderResult;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.cart.stubs.DeliveryServiceStub;
import com.example.eshop.cart.stubs.PaymentServiceStub;
import com.example.eshop.cart.utils.TestDataUtils;
import com.example.eshop.catalog.client.CatalogService;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.warehouse.client.reservationresult.InsufficientQuantityError;
import com.example.eshop.warehouse.client.reservationresult.ReservationError;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaceOrderUsecaseImplTest {
    private final static LocalDateTime CREATION_DATE = LocalDateTime.of(2021, Month.APRIL, 10, 3, 34, 10);

    private Clock clock;
    private CreateOrderDto createOrderDto;
    private Order order;
    private OrderDto orderDto;
    private OrderFactory orderFactory;
    private OrderPlacedEventPublisher orderPlacedEventPublisher;
    private CatalogService catalogService;
    private StockReservationService stockReservationService;
    private OrderMapper orderMapper;

    private final Ean cartItemEan = FakeData.ean();
    private final Money cartItemPrice = Money.USD(3);
    private final int cartItemQuantity = 3;

    @BeforeEach
    void setUp() {
        // clock
        clock = Clock.fixed(CREATION_DATE.atZone(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));

        // createOrderDto
        var customerId = FakeData.customerId();
        var cartDto = FakeData.cartDto();
        var cart = new Cart(customerId);
        cart.addItem(cartItemEan, cartItemPrice, cartItemQuantity);

        createOrderDto = new CreateOrderDto(customerId, cartDto, FakeData.deliveryAddress(),
                new DeliveryServiceId("1"), new PaymentServiceId("1"));

        // orderFactory
        order = new Order(
                UUID.randomUUID(),
                customerId,
                cartDto,
                FakeData.deliveryAddress(),
                new DeliveryServiceStub(true),
                new PaymentServiceStub(true)
        );

        orderFactory = mock(OrderFactory.class);
        when(orderFactory.create(createOrderDto)).thenReturn(order);

        // orderPlacedEventPublisher
        orderPlacedEventPublisher = mock(OrderPlacedEventPublisher.class);

        // reserveStockItemService
        stockReservationService = mock(StockReservationService.class);

        // catalogService
        var skuWithProductDto = TestDataUtils.toSkuWithProductDto(cart.getItem(cartItemEan));
        catalogService = mock(CatalogService.class);
        when(catalogService.getSku(List.of(cartItemEan))).thenReturn(Mono.just(
                Map.of(cartItemEan, skuWithProductDto)
        ));

        // orderMapper
        orderMapper = new OrderMapperImpl();

        orderDto = orderMapper.toOrderDto(order, Map.of(cartItemEan, skuWithProductDto));
    }

    @Test
    void whenPlaceOrder_thenStocksReservedAndOrderCreatedEventIsPublished() {
        // Given
        var domainService = mock(PlaceOrderService.class);
        when(domainService.place(order)).thenReturn(PlaceOrderResult.success(order));

        when(stockReservationService.reserve(orderDto)).thenReturn(ReservationResult.success());

        var service = new PlaceOrderUsecaseImpl(domainService, orderPlacedEventPublisher, orderFactory, clock, catalogService,
                stockReservationService, orderMapper);

        var expectedEvent = new OrderPlacedEvent(orderDto, CREATION_DATE);

        // When
        service.place(createOrderDto);

        // Then
        verify(domainService).place(order);
        verify(stockReservationService).reserve(orderDto);
        verify(orderPlacedEventPublisher).publish(expectedEvent);
    }

    @Test
    void givenInvalidCreateOrderDto_whenPlaceOrder_thenValidationExceptionIsThrownAndNoEventIsPublished() {
        // Given
        var domainService = mock(PlaceOrderService.class);
        var errors = new Errors().addError("field", "message");
        when(domainService.place(order)).thenReturn(PlaceOrderResult.failure(order, errors));

        var service = new PlaceOrderUsecaseImpl(domainService, orderPlacedEventPublisher, orderFactory, clock, catalogService,
                stockReservationService, orderMapper);

        // When
        var exception = catchThrowableOfType(() -> service.place(createOrderDto), ValidationException.class);

        // Then
        verify(domainService).place(order);
        assertThat(exception.getErrors()).isEqualTo(errors);
        verify(stockReservationService, never()).reserve(any());
        verify(orderPlacedEventPublisher, never()).publish(any());
    }

    @Test
    void givenExceededCartItemQuantity_whenPlaceOrder_thenValidationExceptionIsThrown() {
        // Given
        var domainService = mock(PlaceOrderService.class);
        when(domainService.place(order)).thenReturn(PlaceOrderResult.success(order));

        List<ReservationError> reservationErrors = List.of(new InsufficientQuantityError(cartItemEan,
                cartItemQuantity, 0, ""));
        when(stockReservationService.reserve(orderDto)).thenReturn(ReservationResult.failure(reservationErrors));

        var service = new PlaceOrderUsecaseImpl(domainService, orderPlacedEventPublisher, orderFactory, clock, catalogService,
               stockReservationService, orderMapper);

        // When
        var exception = catchThrowableOfType(() -> service.place(createOrderDto), ValidationException.class);

        // Then
        assertThat(exception.getErrors()).isNotEmpty();
        verify(stockReservationService).reserve(orderDto);
        verify(orderPlacedEventPublisher, never()).publish(any());
    }
}
