package com.example.eshop.cart.domain.checkout.delivery;

import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryServiceRepository extends JpaRepository<DeliveryService, DeliveryServiceId> {
}
