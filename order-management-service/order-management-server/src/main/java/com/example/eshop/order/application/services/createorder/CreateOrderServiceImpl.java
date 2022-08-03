package com.example.eshop.order.application.services.createorder;

import com.example.eshop.order.config.MetricsConfig;
import com.example.eshop.order.domain.order.Order;
import com.example.eshop.order.domain.order.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry meterRegistry;

    @Override
    public void create(Order order) {
        log.info("Creating order " + order);

        getSelfFromContext().createInternal(order);

        log.info(order + " created");

        recordMetrics();
    }

    @Transactional
    protected void createInternal(Order order) {    // NOSONAR self-invocation
        orderRepository.save(order);
    }

    @Lookup
    protected CreateOrderServiceImpl getSelfFromContext() {
        return this;
    }

    protected void recordMetrics() {
        meterRegistry.counter(MetricsConfig.ORDERS_CREATED_METRIC_NAME).increment();
    }
}
