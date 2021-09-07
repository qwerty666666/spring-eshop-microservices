package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Domain Service for creating Order from Customer's Cart.
 */
@Service
@RequiredArgsConstructor
public class PlaceOrderService {
    private final ApplicationEventPublisher eventPublisher;
    private final PlaceOrderValidator placeOrderValidator;

    /**
     * Place Order and publish Domain Event
     */
    public void place(Order order) {
        checkOrder(order);

        eventPublisher.publishEvent(new OrderPlacedEvent(order));
    }

    private void checkOrder(Order order) {
        var errors = placeOrderValidator.validate(order);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
