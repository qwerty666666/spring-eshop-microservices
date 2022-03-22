package com.example.eshop.checkout.application.services.checkoutprocess;

import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.checkout.application.services.checkoutprocess.dto.CheckoutForm;
import com.example.eshop.checkout.domain.placeorder.PlaceOrderService;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;

/**
 * Checkout Process is a process where customers can choose
 * different options before place order (like discounts /
 * payments / shipments).
 * <p>
 * This class ensures consistency of input data and provides
 * information about available options. But as it is intermediate
 * step in create order process, and order can be in invalid
 * state for saving. Real validation is performed by
 * {@link PlaceOrderService}.
 */
public interface CheckoutProcessService {
    /**
     * @throws ValidationException if there is invalid field in {@code createOrderDto}
     */
    CheckoutForm process(CreateOrderDto createOrderDto);
}
