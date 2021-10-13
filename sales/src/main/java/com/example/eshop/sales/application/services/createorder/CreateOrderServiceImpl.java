package com.example.eshop.sales.application.services.createorder;

import com.example.eshop.sales.domain.Order;
import com.example.eshop.sales.domain.OrderRepository;
import com.example.eshop.sales.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateOrderServiceImpl implements CreateOrderService {
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void save(Order order) {
        order.setStatus(OrderStatus.PENDING);

        orderRepository.save(order);
    }
}
