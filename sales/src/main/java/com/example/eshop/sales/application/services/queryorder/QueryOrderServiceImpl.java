package com.example.eshop.sales.application.services.queryorder;

import com.example.eshop.sales.domain.Order;
import com.example.eshop.sales.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryOrderServiceImpl implements QueryOrderService {
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#customerId == principal.getCustomerId()")
    public Page<Order> getForCustomer(String customerId, Pageable pageable) {
        return orderRepository.findByCustomerIdWithOrderLines(customerId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getById(UUID orderId) {
        return orderRepository.findByIdWithOrderLines(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " does not exist"));
    }
}
