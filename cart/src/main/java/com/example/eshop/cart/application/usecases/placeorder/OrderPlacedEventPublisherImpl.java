package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OrderPlacedEventPublisherImpl implements OrderPlacedEventPublisher {
    private static final Duration REPLY_TIMEOUT = Duration.ofSeconds(10);

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Override
    public void publish(OrderPlacedEvent event) {
        try {
            var key = event.order().id().toString();

            kafkaTemplate.send(CheckoutApi.ORDER_PLACED_TOPIC, key, event)
                    .get(REPLY_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PublishEventException(e);
        } catch (Exception e) {
            throw new PublishEventException(e);
        }
    }
}
