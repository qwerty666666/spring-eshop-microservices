package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceRepository;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.order.OrderFactoryImpl;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceRepository;
import com.example.eshop.cart.utils.FakeData;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.example.eshop.sharedkernel.domain.Localizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderFactoryImplTest {
    private final DeliveryServiceId notExistedDeliveryId = new DeliveryServiceId("1");
    private final PaymentServiceId notExistedPaymentId = new PaymentServiceId("1");

    private final String customerId = FakeData.customerId();
    private final DeliveryAddress address = FakeData.deliveryAddress();
    private final Cart cart = FakeData.cart();

    private OrderFactory orderFactory;

    @BeforeEach
    void setUp() {
        DeliveryServiceRepository deliveryServiceRepository = mock(DeliveryServiceRepository.class);
        when(deliveryServiceRepository.findById(notExistedDeliveryId)).thenReturn(Optional.empty());

        PaymentServiceRepository paymentServiceRepository = mock(PaymentServiceRepository.class);
        when(paymentServiceRepository.findById(notExistedPaymentId)).thenReturn(Optional.empty());

        orderFactory = new OrderFactoryImpl(deliveryServiceRepository, paymentServiceRepository, mock(Localizer.class));
    }

    @Nested
    @SuppressWarnings({ "ConstantConditions", "SameParameterValue" })
    class ValidationTest {
        @Nested
        class AddressValidationTest {
            @Test
            void nullAddress() {
                assertThrowValidationException(createWithAddress(null));
            }

            private CreateOrderDto createWithAddress(DeliveryAddress address) {
                return CreateOrderDto.builder()
                        .customerId(customerId)
                        .address(address)
                        .cart(cart)
                        .build();
            }

            private void assertThrowValidationException(CreateOrderDto createOrderDto) {
                var exception = catchThrowableOfType(() -> orderFactory.create(createOrderDto), ValidationException.class);

                assertThat(exception.getErrors().hasErrors(CreateOrderDto.ADDRESS_FIELD)).isTrue();
            }
        }

        @Nested
        class CustomerIdValidationTest {
            @Test
            void nullCustomerId() {
                assertThrowValidationException(createWithCustomer(null));
            }

            private CreateOrderDto createWithCustomer(String customerId) {
                return CreateOrderDto.builder()
                        .customerId(customerId)
                        .address(address)
                        .cart(cart)
                        .build();
            }

            private void assertThrowValidationException(CreateOrderDto createOrderDto) {
                var exception = catchThrowableOfType(() -> orderFactory.create(createOrderDto), ValidationException.class);

                assertThat(exception.getErrors().hasErrors(CreateOrderDto.CUSTOMER_ID_FIELD)).isTrue();
            }
        }

        @Nested
        class CartValidationTest {
            @Test
            void nullCart() {
                assertThrowValidationException(createWithCart(null));
            }

            @Test
            void emptyCart() {
                assertThrowValidationException(createWithCart(new Cart(customerId)));
            }

            private CreateOrderDto createWithCart(Cart cart) {
                return CreateOrderDto.builder()
                        .customerId(customerId)
                        .address(address)
                        .cart(cart)
                        .build();
            }

            private void assertThrowValidationException(CreateOrderDto createOrderDto) {
                var exception = catchThrowableOfType(() -> orderFactory.create(createOrderDto), ValidationException.class);

                assertThat(exception.getErrors().hasErrors(CreateOrderDto.CART_FIELD)).isTrue();
            }
        }

        @Nested
        class DeliveryServiceIdValidationTest {
            @Test
            void nonExistedDelivery() {
                assertThrowValidationException(createWithDeliveryId(notExistedDeliveryId));
            }

            private CreateOrderDto createWithDeliveryId(DeliveryServiceId id) {
                return CreateOrderDto.builder()
                        .customerId(customerId)
                        .address(address)
                        .cart(cart)
                        .deliveryServiceId(id)
                        .build();
            }

            private void assertThrowValidationException(CreateOrderDto createOrderDto) {
                var exception = catchThrowableOfType(() -> orderFactory.create(createOrderDto), ValidationException.class);

                assertThat(exception.getErrors().hasErrors(CreateOrderDto.DELIVERY_SERVICE_ID_FIELD)).isTrue();
            }
        }

        @Nested
        class PaymentServiceIdValidationTest {
            @Test
            void nonExistedPayment() {
                assertThrowValidationException(createWithPaymentId(notExistedPaymentId));
            }

            private CreateOrderDto createWithPaymentId(PaymentServiceId id) {
                return CreateOrderDto.builder()
                        .customerId(customerId)
                        .address(address)
                        .cart(cart)
                        .paymentServiceId(id)
                        .build();
            }

            private void assertThrowValidationException(CreateOrderDto createOrderDto) {
                var exception = catchThrowableOfType(() -> orderFactory.create(createOrderDto), ValidationException.class);

                assertThat(exception.getErrors().hasErrors(CreateOrderDto.PAYMENT_SERVICE_ID_FIELD)).isTrue();
            }
        }
    }
}