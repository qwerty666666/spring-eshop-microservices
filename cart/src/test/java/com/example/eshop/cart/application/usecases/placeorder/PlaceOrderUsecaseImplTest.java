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
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.warehouse.client.reservationresult.InsufficientQuantityError;
import com.example.eshop.warehouse.client.reservationresult.ReservationError;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Collections;
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
    private OrderFactory orderFactory;
    private ApplicationEventPublisher eventPublisher;
    private Map<Ean, ProductInfo> productsInfo;
    private ProductInfoProvider productInfoProvider;
    private StockReservationService stockReservationService;

    private final Ean cartItemEan = FakeData.ean();
    private final Money cartItemPrice = Money.USD(3);
    private final int cartItemQuantity = 3;

    @BeforeEach
    void setUp() {
        // clock
        clock = Clock.fixed(CREATION_DATE.atZone(ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"));

        // createOrderDto
        var customerId = FakeData.customerId();
        var cart = new Cart(customerId);
        cart.addItem(cartItemEan, cartItemPrice, cartItemQuantity);

        createOrderDto = new CreateOrderDto(customerId, cart, FakeData.deliveryAddress(),
                new DeliveryServiceId("1"), new PaymentServiceId("1"));

        // orderFactory
        order = new Order(
                UUID.randomUUID(),
                customerId,
                cart,
                FakeData.deliveryAddress(),
                new DeliveryServiceStub(true),
                new PaymentServiceStub(true)
        );

        orderFactory = mock(OrderFactory.class);
        when(orderFactory.create(createOrderDto)).thenReturn(order);

        // eventPublisher
        eventPublisher = mock(ApplicationEventPublisher.class);

        // productInfoProvider
        productsInfo = Map.of(cartItemEan, new ProductInfo("test", Collections.emptyList(), Collections.emptyList()));

        productInfoProvider = mock(ProductInfoProvider.class);
        when(productInfoProvider.getProductsInfo(cart)).thenReturn(productsInfo);

        // reserveStockItemService
        stockReservationService = mock(StockReservationService.class);
    }

    @Test
    void whenPlaceOrder_thenStocksReservedAndOrderCreatedEventIsPublished() {
        // Given
        var domainService = mock(PlaceOrderService.class);
        when(domainService.place(order)).thenReturn(PlaceOrderResult.success(order));

        when(stockReservationService.reserve(order)).thenReturn(ReservationResult.success());

        var service = new PlaceOrderUsecaseImpl(domainService, eventPublisher, orderFactory, clock,
                productInfoProvider, stockReservationService);

        var expectedEvent = new OrderPlacedEvent(order, CREATION_DATE, productsInfo);

        // When
        service.place(createOrderDto);

        // Then
        verify(domainService).place(order);
        verify(stockReservationService).reserve(order);
        verify(eventPublisher).publishEvent(expectedEvent);
    }

    @Test
    void givenInvalidCreateOrderDto_whenPlaceOrder_thenValidationExceptionIsThrownAndNoEventIsPublished() {
        // Given
        var domainService = mock(PlaceOrderService.class);
        var errors = new Errors().addError("field", "message");
        when(domainService.place(order)).thenReturn(PlaceOrderResult.failure(order, errors));

        var service = new PlaceOrderUsecaseImpl(domainService, eventPublisher, orderFactory, clock,
                productInfoProvider, stockReservationService);

        // When
        var exception = catchThrowableOfType(() -> service.place(createOrderDto), ValidationException.class);

        // Then
        verify(domainService).place(order);
        assertThat(exception.getErrors()).isEqualTo(errors);
        verify(stockReservationService, never()).reserve(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void givenExceededCartItemQuantity_whenPlaceOrder_thenValidationExceptionIsThrown() {
        // Given
        var domainService = mock(PlaceOrderService.class);
        when(domainService.place(order)).thenReturn(PlaceOrderResult.success(order));

        List<ReservationError> reservationErrors = List.of(new InsufficientQuantityError(cartItemEan,
                cartItemQuantity, 0, ""));
        when(stockReservationService.reserve(order)).thenReturn(ReservationResult.failure(reservationErrors));

        var service = new PlaceOrderUsecaseImpl(domainService, eventPublisher, orderFactory, clock,
                productInfoProvider, stockReservationService);

        // When
        var exception = catchThrowableOfType(() -> service.place(createOrderDto), ValidationException.class);

        // Then
        assertThat(exception.getErrors()).isNotEmpty();
        verify(stockReservationService).reserve(order);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
