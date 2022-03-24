package com.example.eshop.checkout.domain.delivery;

import com.example.eshop.checkout.domain.delivery.DeliveryService.DeliveryServiceId;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link DeliveryService}
 */
public interface DeliveryServiceRepository {
    /**
     * @return {@link DeliveryService} by given id
     */
    Optional<DeliveryService> findById(DeliveryServiceId id);

    /**
     * @return all {@link DeliveryService}s
     */
    List<DeliveryService> findAll();
}
