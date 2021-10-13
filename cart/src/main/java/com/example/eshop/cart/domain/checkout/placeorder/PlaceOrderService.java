package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Domain Service for creating Order.
 */
@Service
@RequiredArgsConstructor
public class PlaceOrderService {
    private final PlaceOrderValidator placeOrderValidator;

    public void place(Order order) {
        checkOrder(order);
    }

    private void checkOrder(Order order) {
        var errors = placeOrderValidator.validate(order);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
