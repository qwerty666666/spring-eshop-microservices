package com.example.eshop.checkout.infrastructure;

import com.example.eshop.checkout.domain.delivery.DeliveryService;
import com.example.eshop.checkout.domain.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.checkout.domain.delivery.DeliveryServiceRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Just stores {@link DeliveryService}s in in-memory map
 */
public class InMemoryDeliveryServiceRepository implements DeliveryServiceRepository {
    private final Map<DeliveryServiceId, DeliveryService> services;

    public InMemoryDeliveryServiceRepository(List<DeliveryService> services) {
        this.services = services.stream()
                .collect(Collectors.toMap(DeliveryService::getId, Function.identity()));
    }

    @Override
    public Optional<DeliveryService> findById(DeliveryServiceId id) {
        return Optional.ofNullable(services.get(id));
    }

    @Override
    public List<DeliveryService> findAll() {
        return services.values().stream().toList();
    }
}
