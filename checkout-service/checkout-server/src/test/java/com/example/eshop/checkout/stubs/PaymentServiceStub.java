package com.example.eshop.checkout.stubs;

import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.checkout.domain.payment.PaymentService;

public class PaymentServiceStub extends PaymentService {
    private final boolean isSupported;

    public PaymentServiceStub(boolean isSupported) {
        super(new PaymentServiceId("2"), "test payment");
        this.isSupported = isSupported;
    }

    @Override
    public boolean canPay(Order order) {
        return isSupported;
    }
}
