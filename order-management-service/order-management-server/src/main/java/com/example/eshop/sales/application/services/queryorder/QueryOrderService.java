package com.example.eshop.sales.application.services.queryorder;

import com.example.eshop.sales.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface QueryOrderService {
    /**
     * Finds orders for given Customer
     */
    Page<Order> getForCustomer(String customerId, Pageable pageable);

    /**
     * Finds {@link Order} by given ID
     *
     * @throws OrderNotFoundException if order with given ID does not exist
     */
    Order getById(UUID orderId);
}
