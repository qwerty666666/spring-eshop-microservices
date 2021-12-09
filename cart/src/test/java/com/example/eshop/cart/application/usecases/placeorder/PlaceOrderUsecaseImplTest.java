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
import com.example.eshop.warehouse.application.services.reserve.ReserveStockItemService;
import com.example.eshop.warehouse.domain.StockQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
    private ReserveStockItemService reserveStockItemService;

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
        reserveStockItemService = mock(ReserveStockItemService.class);
    }

    @Test
    void whenPlaceOrder_thenStocksReservedAndOrderCreatedEventIsPublished() {
        // Given
        var domainService = mock(PlaceOrderService.class);
        when(domainService.place(order)).thenReturn(PlaceOrderResult.success(order));

        var service = new PlaceOrderUsecaseImpl(domainService, eventPublisher, orderFactory, clock,
                productInfoProvider, reserveStockItemService);

        var expectedEvent = new OrderPlacedEvent(order, CREATION_DATE, productsInfo);

        // When
        service.place(createOrderDto);

        // Then
        verify(domainService).place(order);
        verify(reserveStockItemService).reserve(Map.of(cartItemEan, StockQuantity.of(cartItemQuantity)));
        verify(eventPublisher).publishEvent(expectedEvent);
    }

    @Test
    void givenInvalidCreateOrderDto_whenPlace_thenNoEventIsPublished() {
        // Given
        var domainService = mock(PlaceOrderService.class);
        doThrow(new ValidationException(new Errors())).when(domainService).place(order);

        var service = new PlaceOrderUsecaseImpl(domainService, eventPublisher, orderFactory, clock,
                productInfoProvider, reserveStockItemService);

        // When
        //noinspection ResultOfMethodCallIgnored
        catchThrowableOfType(() -> service.place(createOrderDto), ValidationException.class);

        // Then
        verify(domainService).place(order);
        verify(reserveStockItemService, never()).reserve(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
