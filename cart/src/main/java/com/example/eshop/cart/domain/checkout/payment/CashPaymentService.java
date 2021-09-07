package com.example.eshop.cart.domain.checkout.payment;

import com.example.eshop.cart.domain.checkout.order.Order;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CASH")
public class CashPaymentService extends PaymentService {
    @Override
    public boolean canPay(Order order) {
        return true;
    }
}
