package com.example.eshop.cart.stubs;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;

public class PaymentServiceStub extends PaymentService {
    private final boolean isSupported;

    public PaymentServiceStub(boolean isSupported) {
        this.id = new PaymentServiceId("2");
        this.name = "test payment";
        this.isSupported = isSupported;
    }

    @Override
    public boolean canPay(Order order) {
        return isSupported;
    }
}
