package com.example.eshop.cart.config;

import com.example.eshop.checkout.client.order.OrderDto;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import static org.mockito.Mockito.mock;

@Configuration
@ConditionalOnProperty(value = "kafka.disabled", havingValue = "true")
public class TestKafkaConfig {
    @Bean("cart-stocksReservationKafkaTemplate")
    public ReplyingKafkaTemplate<String, OrderDto, ReservationResult> stocksReservationKafkaTemplate() {
        return mock(ReplyingKafkaTemplate.class);
    }
}
