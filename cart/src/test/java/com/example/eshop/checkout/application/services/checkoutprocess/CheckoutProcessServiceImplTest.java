package com.example.eshop.checkout.application.services.checkoutprocess;

import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.checkout.domain.delivery.DeliveryService;
import com.example.eshop.checkout.domain.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.checkout.domain.delivery.DeliveryServiceRepository;
import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.checkout.domain.order.DeliveryAddress;
import com.example.eshop.checkout.application.services.OrderFactoryImpl;
import com.example.eshop.checkout.domain.payment.PaymentService;
import com.example.eshop.checkout.domain.payment.PaymentService.PaymentServiceId;
import com.example.eshop.checkout.domain.payment.PaymentServiceRepository;
import com.example.eshop.checkout.infrastructure.tests.FakeData;
import com.example.eshop.checkout.stubs.DeliveryServiceStub;
import com.example.eshop.checkout.stubs.PaymentServiceStub;
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
    private CartDto cart;

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

        cart = FakeData.cartDto();
    }

    @Test
    void whenProcess_thenShouldContainsAllSupportedDeliveriesAndPayments() {
        // Given
        var createOrderDto = new CreateOrderDto(customerId, cart, address, SUPPORTED_DELIVERY_ID, SUPPORTED_PAYMENT_ID);

        // When
        var form = checkoutProcessService.process(createOrderDto);

        // Then
        assertThat(form.availableDeliveries()).hasSize(1);
        assertThat(form.availableDeliveries().get(0)).isSameAs(SUPPORTED_DELIVERY_SERVICE);

        assertThat(form.availablePayments()).hasSize(1);
        assertThat(form.availablePayments().get(0)).isSameAs(SUPPORTED_PAYMENT_SERVICE);
    }

    @Test
    void whenProcess_thenTotalSumShouldBeSumOfCartAndDelivery() {
        // Given
        var createOrderDto = new CreateOrderDto(customerId, cart, address, SUPPORTED_DELIVERY_ID, SUPPORTED_PAYMENT_ID);
        var expectedPrice = cart.getTotalPrice().add(DeliveryServiceStub.COST);

        // When
        var form = checkoutProcessService.process(createOrderDto);

        // Then
        assertThat(form.total().totalPrice()).isEqualTo(expectedPrice);
    }
}
