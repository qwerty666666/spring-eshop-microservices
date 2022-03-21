package com.example.eshop.cart.domain.checkout.payment;

import com.example.eshop.cart.domain.checkout.order.Order;

/**
 * Payment with cash. Always available.
 */
public class CashPaymentService extends PaymentService {
    public CashPaymentService(PaymentServiceId id, String name) {
        super(id, name);
    }

    @Override
    public boolean canPay(Order order) {
        return true;
    }
}
