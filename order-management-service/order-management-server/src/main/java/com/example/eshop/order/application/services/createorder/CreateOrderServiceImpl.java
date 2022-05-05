package com.example.eshop.order.application.services.createorder;

import com.example.eshop.order.domain.order.Order;
import com.example.eshop.order.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderServiceImpl implements CreateOrderService {
    private final OrderRepository orderRepository;

    @Override
    public void create(Order order) {
        log.info("Creating order " + order);

        getSelfFromContext().createInternal(order);

        log.info(order + " created");
    }

    @Transactional
    protected void createInternal(Order order) {    // NOSONAR self-invocation
        orderRepository.save(order);
    }

    @Lookup
    protected CreateOrderServiceImpl getSelfFromContext() {
        return null;    // NOSONAR non-null api
    }
}
