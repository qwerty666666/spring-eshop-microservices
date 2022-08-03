package com.example.eshop.checkout.application.services.placeorder.orderplacedevent;

import com.example.eshop.checkout.application.services.PublishEventException;
import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.checkout.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
@RefreshScope
@RequiredArgsConstructor
public class KafkaOrderPlacedEventPublisher implements OrderPlacedEventPublisher {
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final AppProperties appProperties;

    @Override
    public void publish(OrderPlacedEvent event) {
        try {
            var key = event.order().id().toString();
            var timeout = appProperties.getKafka().getOrderPlacedEventPublishedTimeoutMs();

            kafkaTemplate.send(CheckoutApi.ORDER_PLACED_TOPIC, key, event)
                    .get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PublishEventException(e);
        } catch (Exception e) {
            throw new PublishEventException(e);
        }
    }
}
