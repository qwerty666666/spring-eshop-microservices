package com.example.eshop.order.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    public static final String ORDERS_CREATED_METRIC_NAME = "orders_created";

    @Bean
    public Counter ordersCreatedCounter(MeterRegistry meterRegistry) {
        return Counter.builder(ORDERS_CREATED_METRIC_NAME).register(meterRegistry);
    }
}
