package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.stubs.DeliveryServiceStub;
import com.example.eshop.cart.stubs.PaymentServiceStub;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import com.example.eshop.sharedkernel.domain.Localizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;

@SuppressWarnings("SameParameterValue")
class PlaceOrderValidatorTest {
    private static final DeliveryService SUPPORTED_DELIVERY = new DeliveryServiceStub(true);
    private static final DeliveryService NOT_SUPPORTED_DELIVERY = new DeliveryServiceStub(false);

    private static final PaymentService SUPPORTED_PAYMENT = new PaymentServiceStub(true);
    private static final PaymentService NOT_SUPPORTED_PAYMENT = new PaymentServiceStub(false);

    private final String customerId = FakeData.customerId();
    private final String fullname = FakeData.fullname();
    private final Phone phone = FakeData.phone();
    private final String country = FakeData.country();
    private final String city = FakeData.city();
    private final String building = FakeData.building();
    private final DeliveryAddress address = FakeData.deliveryAddress();
    private final Cart cart = FakeData.cart();

    private PlaceOrderValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PlaceOrderValidator(mock(Localizer.class));
    }

    @Test
    void validOrder() {
        var order = new Order(UUID.randomUUID(), customerId, cart, address, SUPPORTED_DELIVERY, SUPPORTED_PAYMENT);

        var errors = validator.validate(order);

        assertThat(errors.isEmpty()).isTrue();
    }

    @Nested
    class AddressTest {
        @Test
        void fullname() {
            assertAll(
                    () -> assertValidationReturnsError("null fullname", createOrderWithFullname(null),
                            PlaceOrderValidator.ADDRESS_FULLNAME_FIELD),
                    () -> assertValidationReturnsError("empty fullname", createOrderWithFullname(""),
                            PlaceOrderValidator.ADDRESS_FULLNAME_FIELD)
            );
        }

        @Test
        void phone() {
            assertValidationReturnsError("null phone", createOrderWithPhone(null),
                            PlaceOrderValidator.ADDRESS_PHONE_FIELD);
        }

        @Test
        void country() {
            assertAll(
                    () -> assertValidationReturnsError("null country", createOrderWithCountry(null),
                            PlaceOrderValidator.ADDRESS_COUNTRY_FIELD),
                    () -> assertValidationReturnsError("empty country", createOrderWithCountry(""),
                            PlaceOrderValidator.ADDRESS_COUNTRY_FIELD)
            );
        }

        @Test
        void city() {
            assertAll(
                    () -> assertValidationReturnsError("null city", createOrderWithCity(null),
                            PlaceOrderValidator.ADDRESS_CITY_FIELD),
                    () -> assertValidationReturnsError("empty city", createOrderWithCity(""),
                            PlaceOrderValidator.ADDRESS_CITY_FIELD)
            );
        }

        @Test
        void building() {
            assertAll(
                    () -> assertValidationReturnsError("null building", createOrderWithBuilding(null),
                            PlaceOrderValidator.ADDRESS_BUILDING_FIELD),
                    () -> assertValidationReturnsError("empty building", createOrderWithBuilding(""),
                            PlaceOrderValidator.ADDRESS_BUILDING_FIELD)
            );
        }

        private Order createOrderWithFullname(@Nullable String fullname) {
            var address = new DeliveryAddress(fullname, phone, country, city, null, building, null);
            return new Order(UUID.randomUUID(), customerId, cart, address, SUPPORTED_DELIVERY, SUPPORTED_PAYMENT);
        }

        private Order createOrderWithPhone(@Nullable Phone phone) {
            var address = new DeliveryAddress(fullname, phone, country, city, null, building, null);
            return new Order(UUID.randomUUID(), customerId, cart, address, SUPPORTED_DELIVERY, SUPPORTED_PAYMENT);
        }

        private Order createOrderWithCountry(@Nullable String country) {
            var address = new DeliveryAddress(fullname, phone, country, city, null, building, null);
            return new Order(UUID.randomUUID(), customerId, cart, address, SUPPORTED_DELIVERY, SUPPORTED_PAYMENT);
        }

        private Order createOrderWithCity(@Nullable String city) {
            var address = new DeliveryAddress(fullname, phone, country, city, null, building, null);
            return new Order(UUID.randomUUID(), customerId, cart, address, SUPPORTED_DELIVERY, SUPPORTED_PAYMENT);
        }

        private Order createOrderWithBuilding(@Nullable String building) {
            var address = new DeliveryAddress(fullname, phone, country, city, null, building, null);
            return new Order(UUID.randomUUID(), customerId, cart, address, SUPPORTED_DELIVERY, SUPPORTED_PAYMENT);
        }
    }

    @Nested
    class DeliveryServiceTest {
        @Test
        void nullDeliveryService() {
            var order = createOrderWithDeliveryService(null);
            assertValidationReturnsError("null delivery", order, PlaceOrderValidator.DELIVERY_SERVICE_FIELD);
        }

        @Test
        void notSupportedDeliveryService() {
            var order = createOrderWithDeliveryService(NOT_SUPPORTED_DELIVERY);
            assertValidationReturnsError("not supported delivery", order, PlaceOrderValidator.DELIVERY_SERVICE_FIELD);
        }

        private Order createOrderWithDeliveryService(@Nullable DeliveryService deliveryService) {
            return new Order(UUID.randomUUID(), customerId, cart, address, deliveryService, SUPPORTED_PAYMENT);
        }
    }

    @Nested
    class PaymentServiceTest {
        @Test
        void nullPaymentService() {
            var order = createOrderWithPaymentService(null);
            assertValidationReturnsError("null payment", order, PlaceOrderValidator.PAYMENT_SERVICE_FIELD);
        }

        @Test
        void notSupportedPaymentService() {
            var order = createOrderWithPaymentService(NOT_SUPPORTED_PAYMENT);
            assertValidationReturnsError("not supported payment", order, PlaceOrderValidator.PAYMENT_SERVICE_FIELD);
        }

        private Order createOrderWithPaymentService(@Nullable PaymentService paymentService) {
            return new Order(UUID.randomUUID(), customerId, cart, address, SUPPORTED_DELIVERY, paymentService);
        }
    }

    @Nested
    @SuppressWarnings("ConstantConditions")
    class CartTest {
        @Test
        void nullCart() {
            var order = createOrderWithCart(null);
            assertValidationReturnsError("null cart", order, PlaceOrderValidator.CART_FIELD);
        }

        @Test
        void notSupportedPaymentService() {
            var order = createOrderWithCart(new Cart(customerId));
            assertValidationReturnsError("empty cart", order, PlaceOrderValidator.CART_FIELD);
        }

        private Order createOrderWithCart(@Nullable Cart cart) {
            return new Order(UUID.randomUUID(), customerId, cart, address, SUPPORTED_DELIVERY, SUPPORTED_PAYMENT);
        }
    }

    private void assertValidationReturnsError(String description, Order order, String expectedField) {
        var errors = validator.validate(order);

        assertThat(errors.hasErrors(expectedField)).as(description).isTrue();
    }
}
