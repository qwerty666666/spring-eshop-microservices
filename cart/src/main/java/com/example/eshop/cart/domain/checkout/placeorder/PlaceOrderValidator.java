package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceOrderValidator {
    public static final String CART_FIELD = "cart";
    public static final String CART_ITEMS_FIELD = CART_FIELD + ".items";
    public static final String ADDRESS_FIELD = "address";
    public static final String ADDRESS_FULLNAME_FIELD = ADDRESS_FIELD + ".fullname";
    public static final String ADDRESS_PHONE_FIELD = ADDRESS_FIELD + ".phone";
    public static final String ADDRESS_COUNTRY_FIELD = ADDRESS_FIELD + ".country";
    public static final String ADDRESS_CITY_FIELD = ADDRESS_FIELD + ".city";
    public static final String ADDRESS_BUILDING_FIELD = ADDRESS_FIELD + ".building";
    public static final String DELIVERY_SERVICE_FIELD = "deliveryService";
    public static final String PAYMENT_SERVICE_FIELD = "paymentService";

    /**
     * Check if {@code order} can be placed.
     */
    public Errors validate(Order order) {
        var errors = new Errors();

        validateCart(order, errors);
        validateAddress(order, errors);
        validateDelivery(order, errors);
        validatePayment(order, errors);

        return errors;
    }

    private void validateCart(Order order, Errors errors) {
        var cart = order.getCart();

        if (cart == null) {
            errors.addError(CART_FIELD, "cart.null");
            return;
        }

        if (cart.getItems().isEmpty()) {
            errors.addError(CART_FIELD, "cart.empty");
        }
    }

    private void validateAddress(Order order, Errors errors) {
        var address = order.getAddress();

        if (address == null) {
            errors.addError(ADDRESS_FIELD, "address.null");
            // do not validate location if address is null
            return;
        }

        if (address.fullname() == null || address.fullname().isEmpty()) { // NOSONAR npe
            errors.addError(ADDRESS_FULLNAME_FIELD, "address.fullname.empty");
        }
        if (address.phone() == null) {
            errors.addError(ADDRESS_PHONE_FIELD, "address.phone.null");
        }

        // street and flat can be empty
        if (address.country() == null || address.country().isEmpty()) { // NOSONAR npe
            errors.addError(ADDRESS_COUNTRY_FIELD, "address.country.empty");
        }
        if (address.city() == null || address.city().isEmpty()) { // NOSONAR npe
            errors.addError(ADDRESS_CITY_FIELD, "address.city.empty");
        }
        if (address.building() == null || address.building().isEmpty()) { // NOSONAR npe
            errors.addError(ADDRESS_BUILDING_FIELD, "address.building.empty");
        }
    }

    private void validateDelivery(Order order, Errors errors) {
        var deliveryService = order.getDeliveryService();

        if (deliveryService == null) {
            errors.addError(DELIVERY_SERVICE_FIELD, "delivery.null");
            return;
        }

        if (!deliveryService.canDeliver(order)) {
            errors.addError(DELIVERY_SERVICE_FIELD, "delivery.notAvailable");
        }
    }

    private void validatePayment(Order order, Errors errors) {
        var paymentService = order.getPaymentService();

        if (paymentService == null) {
            errors.addError(PAYMENT_SERVICE_FIELD, "payment.null");
            return;
        }

        if (!paymentService.canPay(order)) {
            errors.addError(PAYMENT_SERVICE_FIELD, "payment.notAvailable");
        }
    }
}
