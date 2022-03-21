package com.example.eshop.cart.domain.checkout.payment;

import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link PaymentService}
 */
public interface PaymentServiceRepository {
    /**
     * @return {@link PaymentService} by given id
     */
    Optional<PaymentService> findById(PaymentServiceId id);

    /**
     * @return all {@link PaymentService}s
     */
    List<PaymentService> findAll();
}