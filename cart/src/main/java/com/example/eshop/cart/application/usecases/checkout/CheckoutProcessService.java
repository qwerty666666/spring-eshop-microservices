package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;

/**
 * Checkout Process is a process where customers can choose
 * different options before place order (like discounts /
 * payments / shipments).
 * <p>
 * This class ensures consistency of input data and provides
 * information about available options. But as it is intermediate
 * step in create order process, order can be set to invalid
 * for saving state. Real validation is performed by
 * {@link PlaceOrderService}.
 */
public interface CheckoutProcessService {
    /**
     * @throws ValidationException if there is invalid field in {@code createOrderDto}
     */
    CheckoutForm process(CreateOrderDto createOrderDto);
}
