package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceOrderValidator {
    public static final String CART_FIELD = "cart";
    public static final String ADDRESS_FIELD = "address";
    public static final String ADDRESS_FULLNAME_FIELD = ADDRESS_FIELD + ".fullname";
    public static final String ADDRESS_PHONE_FIELD = ADDRESS_FIELD + ".phone";
    public static final String ADDRESS_COUNTRY_FIELD = ADDRESS_FIELD + ".country";
    public static final String ADDRESS_CITY_FIELD = ADDRESS_FIELD + ".city";
    public static final String ADDRESS_BUILDING_FIELD = ADDRESS_FIELD + ".building";
    public static final String DELIVERY_SERVICE_FIELD = "deliveryService";
    public static final String PAYMENT_SERVICE_FIELD = "paymentService";

    private final MessageSource messageSource;

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
            errors.addError(CART_FIELD, messageSource.getMessage("cart.null", null,
                    LocaleContextHolder.getLocale()));
            return;
        }

        if (cart.isEmpty()) {
            errors.addError(CART_FIELD, messageSource.getMessage("cart.empty", null,
                    LocaleContextHolder.getLocale()));
        }
    }

    private void validateAddress(Order order, Errors errors) {
        var address = order.getAddress();

        if (address == null) {
            errors.addError(ADDRESS_FIELD, messageSource.getMessage("address.null", null,
                    LocaleContextHolder.getLocale()));
            // do not validate location if address is null
            return;
        }

        if (address.fullname() == null || address.fullname().isEmpty()) {
            errors.addError(ADDRESS_FULLNAME_FIELD, messageSource.getMessage("address.fullname.empty", null,
                    LocaleContextHolder.getLocale()));
        }
        if (address.phone() == null) {
            errors.addError(ADDRESS_PHONE_FIELD, messageSource.getMessage("address.phone.null", null,
                    LocaleContextHolder.getLocale()));
        }

        // street and flat can be empty
        if (address.country() == null || address.country().isEmpty()) {
            errors.addError(ADDRESS_COUNTRY_FIELD, messageSource.getMessage("address.country.empty", null,
                    LocaleContextHolder.getLocale()));
        }
        if (address.city() == null || address.city().isEmpty()) {
            errors.addError(ADDRESS_CITY_FIELD, messageSource.getMessage("address.city.empty", null,
                    LocaleContextHolder.getLocale()));
        }
        if (address.building() == null || address.building().isEmpty()) {
            errors.addError(ADDRESS_BUILDING_FIELD, messageSource.getMessage("address.building.empty", null,
                    LocaleContextHolder.getLocale()));
        }
    }

    private void validateDelivery(Order order, Errors errors) {
        var deliveryService = order.getDeliveryService();

        if (deliveryService == null) {
            errors.addError(DELIVERY_SERVICE_FIELD, messageSource.getMessage("delivery.null", null,
                    LocaleContextHolder.getLocale()));
            return;
        }

        if (!deliveryService.canDeliver(order)) {
            errors.addError(DELIVERY_SERVICE_FIELD, messageSource.getMessage("delivery.notAvailable",
                    new Object[]{ deliveryService.getName() }, LocaleContextHolder.getLocale()));
        }
    }

    private void validatePayment(Order order, Errors errors) {
        var paymentService = order.getPaymentService();

        if (paymentService == null) {
            errors.addError(PAYMENT_SERVICE_FIELD, messageSource.getMessage("payment.null", null,
                    LocaleContextHolder.getLocale()));
            return;
        }

        if (!paymentService.canPay(order)) {
            errors.addError(PAYMENT_SERVICE_FIELD, messageSource.getMessage("payment.notAvailable",
                    new Object[] { paymentService.getName() }, LocaleContextHolder.getLocale()));
        }
    }
}
