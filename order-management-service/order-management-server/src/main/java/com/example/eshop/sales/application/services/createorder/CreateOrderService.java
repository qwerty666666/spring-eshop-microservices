package com.example.eshop.sales.application.services.createorder;

import com.example.eshop.sales.domain.order.Order;

public interface CreateOrderService {
    void save(Order order);
}
