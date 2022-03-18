package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceRepository;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.OrderFactoryImpl;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceRepository;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.cart.stubs.DeliveryServiceStub;
import com.example.eshop.cart.stubs.PaymentServiceStub;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckoutProcessServiceImplTest {
    private static final DeliveryServiceId SUPPORTED_DELIVERY_ID = new DeliveryServiceId("1");
    private static final PaymentServiceId SUPPORTED_PAYMENT_ID = new PaymentServiceId("1");

    private final DeliveryService SUPPORTED_DELIVERY_SERVICE = new DeliveryServiceStub(true);
    private final DeliveryService NOT_SUPPORTED_DELIVERY_SERVICE = new DeliveryServiceStub(false);

    private final PaymentService SUPPORTED_PAYMENT_SERVICE = new PaymentServiceStub(true);
    private final PaymentService NOT_SUPPORTED_PAYMENT_SERVICE = new PaymentServiceStub(false);

    private final String customerId = FakeData.customerId();
    private final DeliveryAddress address = FakeData.deliveryAddress();
    private Cart cart;

    private CheckoutProcessServiceImpl checkoutProcessService;

    @BeforeEach
    void setUp() {
        // CheckoutProcessService

        var deliveryServiceRepository = mock(DeliveryServiceRepository.class);
        when(deliveryServiceRepository.findById(SUPPORTED_DELIVERY_ID)).thenReturn(Optional.of(SUPPORTED_DELIVERY_SERVICE));
        when(deliveryServiceRepository.findAll()).thenReturn(List.of(SUPPORTED_DELIVERY_SERVICE, NOT_SUPPORTED_DELIVERY_SERVICE));

        var paymentServiceRepository = mock(PaymentServiceRepository.class);
        when(paymentServiceRepository.findById(SUPPORTED_PAYMENT_ID)).thenReturn(Optional.of(SUPPORTED_PAYMENT_SERVICE));
        when(paymentServiceRepository.findAll()).thenReturn(List.of(SUPPORTED_PAYMENT_SERVICE, NOT_SUPPORTED_PAYMENT_SERVICE));

        var orderFactory = new OrderFactoryImpl(deliveryServiceRepository, paymentServiceRepository);

        checkoutProcessService = new CheckoutProcessServiceImpl(deliveryServiceRepository, paymentServiceRepository, orderFactory);

        // Cart

        cart = FakeData.cart(customerId);

        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(customerId)).thenReturn(Optional.of(cart));
    }

    @Test
    void whenProcess_thenShouldContainsAllSupportedDeliveriesAndPayments() {
        // Given
        var createOrderDto = new CreateOrderDto(customerId, cart, address, SUPPORTED_DELIVERY_ID, SUPPORTED_PAYMENT_ID);

        // When
        var form = checkoutProcessService.process(createOrderDto);

        // Then
        assertThat(form.getAvailableDeliveries()).hasSize(1);
        assertThat(form.getAvailableDeliveries().get(0)).isSameAs(SUPPORTED_DELIVERY_SERVICE);

        assertThat(form.getAvailablePayments()).hasSize(1);
        assertThat(form.getAvailablePayments().get(0)).isSameAs(SUPPORTED_PAYMENT_SERVICE);
    }

    @Test
    void whenProcess_thenTotalSumShouldBeSumOfCartAndDelivery() {
        // Given
        var createOrderDto = new CreateOrderDto(customerId, cart, address, SUPPORTED_DELIVERY_ID, SUPPORTED_PAYMENT_ID);
        var expectedPrice = cart.getTotalPrice().add(DeliveryServiceStub.COST);

        // When
        var form = checkoutProcessService.process(createOrderDto);

        // Then
        assertThat(form.getTotal().getTotalPrice()).isEqualTo(expectedPrice);
    }
}
