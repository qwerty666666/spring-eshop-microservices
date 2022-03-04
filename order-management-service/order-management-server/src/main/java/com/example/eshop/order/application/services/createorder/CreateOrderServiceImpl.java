package com.example.eshop.order.application.services.createorder;

import com.example.eshop.order.domain.order.Order;
import com.example.eshop.order.domain.order.OrderRepository;
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
        orderRepository.save(order);
    }
}
