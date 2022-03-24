package com.example.eshop.checkout.domain.payment;

import com.example.eshop.checkout.domain.order.Order;

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
