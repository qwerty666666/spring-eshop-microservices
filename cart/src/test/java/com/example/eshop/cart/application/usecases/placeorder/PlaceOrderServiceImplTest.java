package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.application.usecases.cartquery.CartNotFoundException;
import com.example.eshop.cart.application.usecases.checkout.OrderDto;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.order.OrderFactoryImpl;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceRepository;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceRepository;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService;
import com.example.eshop.cart.stubs.DeliveryServiceStub;
import com.example.eshop.cart.stubs.PaymentServiceStub;
import com.example.eshop.cart.utils.CartUtils;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaceOrderServiceImplTest {
    private final static DeliveryServiceId DELIVERY_SERVICE_ID = new DeliveryServiceId("1");
    private final static PaymentServiceId PAYMENT_SERVICE_ID = new PaymentServiceId("1");
    private final static DeliveryService DELIVERY_SERVICE = new DeliveryServiceStub(true);
    private final static PaymentService PAYMENT_SERVICE = new PaymentServiceStub(true);

    private final String customerId = "customerId";
    private Cart customerCart;
    private Cart initialCart;
    private OrderFactory orderFactory;
    private final DeliveryAddress address = new DeliveryAddress("fullname", Phone.fromString("+79993334444"), "country", "city", null, "building", null);

    @BeforeEach
    void setUp() {
        initialCart = new Cart(customerId);
        initialCart.addItem(Ean.fromString("1234567890123"), Money.USD(10), 10, "Sneakers");

        customerCart = initialCart.clone();

        var deliveryServiceRepository = mock(DeliveryServiceRepository.class);
        when(deliveryServiceRepository.findById(DELIVERY_SERVICE_ID)).thenReturn(Optional.of(DELIVERY_SERVICE));

        var paymentServiceRepository = mock(PaymentServiceRepository.class);
        when(paymentServiceRepository.findById(PAYMENT_SERVICE_ID)).thenReturn(Optional.of(PAYMENT_SERVICE));

        orderFactory = new OrderFactoryImpl(deliveryServiceRepository, paymentServiceRepository);
    }

    @Test
    void whenCustomerHasNoCart_thenThrowCartNotFoundException() {
        // Given
        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(eq(customerId))).thenReturn(Optional.empty());

        var placeOrderDomainService = mock(PlaceOrderService.class);

        var service = new PlaceOrderServiceImpl(cartRepository, placeOrderDomainService, orderFactory);
        var orderDto = new OrderDto(customerId, address, DELIVERY_SERVICE_ID, PAYMENT_SERVICE_ID);

        // When + Then
        assertThatThrownBy(() -> service.place(orderDto))
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void whenPlaceOrder_thenOrderCreatedWithCustomersCart() {
        // Given
        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(eq(customerId))).thenReturn(Optional.of(customerCart));

        var placeOrderDomainService = mock(PlaceOrderService.class);
        var service = new PlaceOrderServiceImpl(cartRepository, placeOrderDomainService, orderFactory);
        var orderDto = new OrderDto(customerId, address, DELIVERY_SERVICE_ID, PAYMENT_SERVICE_ID);

        // When
        service.place(orderDto);

        // Then
        var captor = ArgumentCaptor.forClass(Order.class);

        verify(placeOrderDomainService).place(captor.capture());

        CartUtils.assertCartsHasTheSameItems(initialCart, captor.getValue().getCart());
    }

    @Test
    void whenPlaceOrder_thenCustomersCartIsCleared() {
        // Given
        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(eq(customerId))).thenReturn(Optional.of(customerCart));

        var placeOrderDomainService = mock(PlaceOrderService.class);
        var service = new PlaceOrderServiceImpl(cartRepository, placeOrderDomainService, orderFactory);
        var orderDto = new OrderDto(customerId, address, DELIVERY_SERVICE_ID, PAYMENT_SERVICE_ID);

        // When
        service.place(orderDto);

        // Then
        assertThat(customerCart.isEmpty()).isTrue();
    }
}
