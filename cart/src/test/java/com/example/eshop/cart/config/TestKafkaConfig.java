package com.example.eshop.cart.config;

import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import static org.mockito.Mockito.mock;

@Configuration
@ConditionalOnProperty(value = KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY, havingValue = "true")
public class TestKafkaConfig {
    @Bean
    public ReplyingKafkaTemplate<String, OrderDto, ReservationResult> stocksReservationKafkaTemplate() {
        return mock(ReplyingKafkaTemplate.class);
    }

    @Bean
    public KafkaTemplate<String, OrderPlacedEvent> orderPlacedEventKafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}
