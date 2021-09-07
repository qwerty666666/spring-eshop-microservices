package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceRepository;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotAvailableException;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentNotAvailableException;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.order.OrderFactoryImpl;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceRepository;
import com.example.eshop.cart.stubs.DeliveryServiceStub;
import com.example.eshop.cart.stubs.PaymentServiceStub;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderFactoryImplTest {
    private static final DeliveryServiceId SUPPORTED_DELIVERY_ID = new DeliveryServiceId("1");
    private static final DeliveryServiceId NOT_SUPPORTED_DELIVERY_ID = new DeliveryServiceId("2");
    private static final DeliveryServiceId NOT_EXISTED_DELIVERY_ID = new DeliveryServiceId("3");

    private static final PaymentServiceId SUPPORTED_PAYMENT_ID = new PaymentServiceId("1");
    private static final PaymentServiceId NOT_SUPPORTED_PAYMENT_ID = new PaymentServiceId("2");
    private static final PaymentServiceId NOT_EXISTED_PAYMENT_ID = new PaymentServiceId("3");

    private static final DeliveryService SUPPORTED_DELIVERY = new DeliveryServiceStub(true);
    private static final DeliveryService NOT_SUPPORTED_DELIVERY = new DeliveryServiceStub(false);

    private static final PaymentService SUPPORTED_PAYMENT = new PaymentServiceStub(true);
    private static final PaymentService NOT_SUPPORTED_PAYMENT = new PaymentServiceStub(false);

    private final DeliveryAddress address = new DeliveryAddress("fullname", Phone.fromString("+79993334444"), "country", "city", null, "building", null);
    private Cart cart;
    private OrderFactory orderFactory;

    @BeforeEach
    void setUp() {
        DeliveryServiceRepository deliveryServiceRepository = mock(DeliveryServiceRepository.class);
        when(deliveryServiceRepository.findById(SUPPORTED_DELIVERY_ID)).thenReturn(Optional.of(SUPPORTED_DELIVERY));
        when(deliveryServiceRepository.findById(NOT_SUPPORTED_DELIVERY_ID)).thenReturn(Optional.of(NOT_SUPPORTED_DELIVERY));
        when(deliveryServiceRepository.findById(NOT_EXISTED_DELIVERY_ID)).thenReturn(Optional.empty());

        PaymentServiceRepository paymentServiceRepository = mock(PaymentServiceRepository.class);
        when(paymentServiceRepository.findById(SUPPORTED_PAYMENT_ID)).thenReturn(Optional.of(SUPPORTED_PAYMENT));
        when(paymentServiceRepository.findById(NOT_SUPPORTED_PAYMENT_ID)).thenReturn(Optional.of(NOT_SUPPORTED_PAYMENT));
        when(paymentServiceRepository.findById(NOT_EXISTED_PAYMENT_ID)).thenReturn(Optional.empty());

        orderFactory = new OrderFactoryImpl(deliveryServiceRepository, paymentServiceRepository);

        cart = new Cart("CustomerId");
        cart.addItem(Ean.fromString("1234567890123"), Money.USD(123), 10, "test");
    }

    @Test
    void givenNonSupportedDelivery_whenCreateOrder_thenThrowShipmentNotAvailableException() {
        assertThatThrownBy(() -> orderFactory.create(new PlaceOrderDto("id", cart, address, NOT_SUPPORTED_DELIVERY_ID, SUPPORTED_PAYMENT_ID)))
                .isInstanceOf(ShipmentNotAvailableException.class);
    }

    @Test
    void givenNonExistingDelivery_whenCreateOrder_thenThrowDeliveryNotFoundException() {
        assertThatThrownBy(() -> orderFactory.create(new PlaceOrderDto("id", cart, address, NOT_EXISTED_DELIVERY_ID, SUPPORTED_PAYMENT_ID)))
                .isInstanceOf(DeliveryServiceNotFoundException.class);
    }

    @Test
    void givenNonSupportedPayment_whenCreateOrder_thenThrowPaymentNotAvailableException() {

        assertThatThrownBy(() -> orderFactory.create(new PlaceOrderDto("id", cart, address, SUPPORTED_DELIVERY_ID, NOT_SUPPORTED_PAYMENT_ID)))
                .isInstanceOf(PaymentServiceNotAvailableException.class);
    }

    @Test
    void givenNonExistingPayment_whenCreateOrder_thenThrowPaymentNotFoundException() {
        assertThatThrownBy(() -> orderFactory.create(new PlaceOrderDto("id", cart, address, SUPPORTED_DELIVERY_ID, NOT_EXISTED_PAYMENT_ID)))
                .isInstanceOf(PaymentServiceNotFoundException.class);
    }
}