package com.example.eshop.order.application.services.createorder;

import com.example.eshop.order.domain.order.Order;

public interface CreateOrderService {
    void save(Order order);
}
