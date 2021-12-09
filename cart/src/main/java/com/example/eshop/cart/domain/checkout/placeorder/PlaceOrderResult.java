package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The result of place order operation
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PlaceOrderResult {
    private final Order order;
    private final Errors errors;

    /**
     * @return new Success Result
     */
    public static PlaceOrderResult success(Order order) {
        return new PlaceOrderResult(order, Errors.empty());
    }

    /**
     * @return new Failure Result
     */
    public static PlaceOrderResult failure(Order order, Errors errors) {
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("errors must be non empty");
        }

        return new PlaceOrderResult(order, errors);
    }

    /**
     * @return if Order was placed successfully
     */
    public boolean isSuccess() {
        return errors.isEmpty();
    }
}
