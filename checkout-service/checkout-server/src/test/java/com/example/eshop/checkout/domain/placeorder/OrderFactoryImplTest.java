package com.example.eshop.checkout.domain.placeorder;

import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.checkout.domain.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.checkout.domain.delivery.DeliveryServiceRepository;
import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.checkout.domain.order.DeliveryAddress;
import com.example.eshop.checkout.application.services.OrderFactory;
import com.example.eshop.checkout.application.services.OrderFactoryImpl;
import com.example.eshop.checkout.domain.payment.PaymentService.PaymentServiceId;
import com.example.eshop.checkout.domain.payment.PaymentServiceRepository;
import com.example.eshop.checkout.FakeData;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
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
    private final CartDto cart = FakeData.cartDto();

    private OrderFactory orderFactory;

    @BeforeEach
    void setUp() {
        DeliveryServiceRepository deliveryServiceRepository = mock(DeliveryServiceRepository.class);
        when(deliveryServiceRepository.findById(notExistedDeliveryId)).thenReturn(Optional.empty());

        PaymentServiceRepository paymentServiceRepository = mock(PaymentServiceRepository.class);
        when(paymentServiceRepository.findById(notExistedPaymentId)).thenReturn(Optional.empty());

        orderFactory = new OrderFactoryImpl(deliveryServiceRepository, paymentServiceRepository);
    }

    @Nested
    @SuppressWarnings({ "ConstantConditions", "SameParameterValue" })
    class ValidationTest {
        @Nested
        class AddressValidationTest {
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
            void emptyCart() {
                assertThrowValidationException(createWithCart(FakeData.emptyCartDto()));
            }

            private CreateOrderDto createWithCart(CartDto cart) {
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