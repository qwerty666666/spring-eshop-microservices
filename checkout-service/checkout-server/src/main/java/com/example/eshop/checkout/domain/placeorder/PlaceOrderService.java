package com.example.eshop.checkout.domain.placeorder;

import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.sharedkernel.domain.base.DomainService;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Domain Service for place Order.
 * <p>
 * This service check that Order satisfies to all domain rules.
 */
@Service
@RequiredArgsConstructor
public class PlaceOrderService implements DomainService {
    private final PlaceOrderValidator placeOrderValidator;

    public PlaceOrderResult place(Order order) {
        var validationErrors = checkOrder(order);

        if (!validationErrors.isEmpty()) {
            return PlaceOrderResult.failure(order, validationErrors);
        }

        return PlaceOrderResult.success(order);
    }

    private Errors checkOrder(Order order) {
        return placeOrderValidator.validate(order);
    }
}
