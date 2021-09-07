package com.example.eshop.cart.domain.checkout.payment;

import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentServiceRepository extends JpaRepository<PaymentService, PaymentServiceId> {
}
