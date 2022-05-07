package com.example.eshop.order.application.eventlisteners;

import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.order.application.services.createorder.CreateOrderService;
import com.example.eshop.order.infrastructure.messaging.ProcessedMessage;
import com.example.eshop.order.infrastructure.messaging.ProcessedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPlacedEventListener {
    private final OrderPlacedEventMapper mapper;
    private final CreateOrderService createOrderService;
    private final ProcessedMessageRepository processedMessageRepository;

    @KafkaListener(
            topics = CheckoutApi.ORDER_PLACED_TOPIC,
            containerFactory = "orderPlacedKafkaListenerContainerFactory"
    )
    @Transactional
    public void onOrderPlaced(@Payload OrderPlacedEvent event, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        if (processedMessageRepository.existsByMessageKeyAndMessageClass(key, OrderPlacedEvent.class)) {
            log.trace("Skip duplicated OrderPlacedEvent: " + event);
            return;
        }

        log.trace("Process OrderPlacedEvent: " + event);

        processEvent(event);

        processedMessageRepository.save(new ProcessedMessage(key, OrderPlacedEvent.class));
    }

    private void processEvent(OrderPlacedEvent event) {
        var order = mapper.toOrder(event);

        createOrderService.create(order);
    }
}
