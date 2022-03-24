package com.example.eshop.checkout.infrastructure;

import com.example.eshop.checkout.domain.payment.PaymentService;
import com.example.eshop.checkout.domain.payment.PaymentService.PaymentServiceId;
import com.example.eshop.checkout.domain.payment.PaymentServiceRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Just stores {@link PaymentService}s in in-memory map
 */
public class InMemoryPaymentServiceRepository implements PaymentServiceRepository {
    private final Map<PaymentServiceId, PaymentService> services;

    public InMemoryPaymentServiceRepository(List<PaymentService> services) {
        this.services = services.stream()
                .collect(Collectors.toMap(PaymentService::getId, Function.identity()));
    }

    @Override
    public Optional<PaymentService> findById(PaymentServiceId id) {
        return Optional.ofNullable(services.get(id));
    }

    @Override
    public List<PaymentService> findAll() {
        return services.values().stream().toList();
    }
}
