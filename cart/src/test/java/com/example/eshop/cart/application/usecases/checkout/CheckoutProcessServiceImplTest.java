package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.application.usecases.cartquery.CartNotFoundException;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.order.OrderFactoryImpl;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceRepository;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceRepository;
import com.example.eshop.cart.stubs.DeliveryServiceStub;
import com.example.eshop.cart.stubs.PaymentServiceStub;
import com.example.eshop.cart.utils.CartUtils;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckoutProcessServiceImplTest {
    private static final DeliveryServiceId SUPPORTED_DELIVERY_ID = new DeliveryServiceId("1");
    private static final PaymentServiceId SUPPORTED_PAYMENT_ID = new PaymentServiceId("1");

    private final DeliveryService SUPPORTED_DELIVERY_SERVICE = new DeliveryServiceStub(true);
    private final DeliveryService NOT_SUPPORTED_DELIVERY_SERVICE = new DeliveryServiceStub(false);

    private final PaymentService SUPPORTED_PAYMENT_SERVICE = new PaymentServiceStub(true);
    private final PaymentService NOT_SUPPORTED_PAYMENT_SERVICE = new PaymentServiceStub(false);

    private final String customerId = "customerId";
    private final DeliveryAddress address = new DeliveryAddress("fullname", Phone.fromString("+79993334444"), "country", "city", null, "building", null);
    private final Money cartItemPrice = Money.USD(10);
    private Cart cart;

    private DeliveryServiceRepository deliveryServiceRepository;
    private PaymentServiceRepository paymentServiceRepository;
    private OrderFactory orderFactory;
    private CheckoutProcessServiceImpl checkoutProcessService;

    @BeforeEach
    void setUp() {
        deliveryServiceRepository = mock(DeliveryServiceRepository.class);
        when(deliveryServiceRepository.findById(SUPPORTED_DELIVERY_ID)).thenReturn(Optional.of(SUPPORTED_DELIVERY_SERVICE));
        when(deliveryServiceRepository.findAll()).thenReturn(List.of(SUPPORTED_DELIVERY_SERVICE, NOT_SUPPORTED_DELIVERY_SERVICE));

        paymentServiceRepository = mock(PaymentServiceRepository.class);
        when(paymentServiceRepository.findById(SUPPORTED_PAYMENT_ID)).thenReturn(Optional.of(SUPPORTED_PAYMENT_SERVICE));
        when(paymentServiceRepository.findAll()).thenReturn(List.of(SUPPORTED_PAYMENT_SERVICE, NOT_SUPPORTED_PAYMENT_SERVICE));

        orderFactory = new OrderFactoryImpl(deliveryServiceRepository, paymentServiceRepository);

        cart = new Cart(customerId);
        cart.addItem(Ean.fromString("1234567890123"), cartItemPrice, 10, "test");

        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(eq(customerId))).thenReturn(Optional.of(cart));

        checkoutProcessService = new CheckoutProcessServiceImpl(cartRepository, deliveryServiceRepository,
                paymentServiceRepository, orderFactory);
    }

    @Test
    void whenCustomerHasNoCart_thenThrowCartNotFoundException() {
        // Given
        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(eq(customerId))).thenReturn(Optional.empty());

        var checkoutProcessService = new CheckoutProcessServiceImpl(cartRepository, deliveryServiceRepository,
                paymentServiceRepository, orderFactory);

        var orderDto = new OrderDto(customerId, address, SUPPORTED_DELIVERY_ID, SUPPORTED_PAYMENT_ID);

        // When + Then
        assertThatThrownBy(() -> checkoutProcessService.process(orderDto))
                .isInstanceOf(CartNotFoundException.class);
    }

    @Test
    void whenProcessOrder_thenOrderCreatedWithCustomersCart() {
        // Given
        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(eq(customerId))).thenReturn(Optional.of(cart));

        var checkoutProcessService = new CheckoutProcessServiceImpl(cartRepository, deliveryServiceRepository,
                paymentServiceRepository, orderFactory);

        var orderDto = new OrderDto(customerId, address, SUPPORTED_DELIVERY_ID, SUPPORTED_PAYMENT_ID);

        // When
        var form = checkoutProcessService.process(orderDto);

        // Then
        CartUtils.assertCartsHasTheSameItems(cart, form.getOrder().getCart());
    }

    @Test
    void whenProcess_thenShouldContainsAllSupportedDeliveriesAndPayments() {
        // Given
        var orderDto = new OrderDto(customerId, address, SUPPORTED_DELIVERY_ID, SUPPORTED_PAYMENT_ID);

        // When
        var form = checkoutProcessService.process(orderDto);

        // Then
        assertThat(form.getAvailableDeliveries()).hasSize(1);
        assertThat(form.getAvailableDeliveries().get(0)).isSameAs(SUPPORTED_DELIVERY_SERVICE);

        assertThat(form.getAvailablePayments()).hasSize(1);
        assertThat(form.getAvailablePayments().get(0)).isSameAs(SUPPORTED_PAYMENT_SERVICE);
    }

    @Test
    void whenProcess_thenTotalSumShouldBeSumOfCartAndDelivery() {
        // Given
        var orderDto = new OrderDto(customerId, address, SUPPORTED_DELIVERY_ID, SUPPORTED_PAYMENT_ID);
        var expectedPrice = cartItemPrice.add(DeliveryServiceStub.COST);

        // When
        var form = checkoutProcessService.process(orderDto);

        // Then
        assertThat(form.getTotal().getTotalPrice()).isEqualTo(expectedPrice);
    }
}
