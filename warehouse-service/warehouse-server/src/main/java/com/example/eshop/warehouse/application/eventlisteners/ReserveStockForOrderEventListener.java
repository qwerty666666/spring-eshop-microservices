package com.example.eshop.warehouse.application.eventlisteners;

import com.example.eshop.cart.client.model.CartItemDto;
import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.warehouse.application.services.reserve.ReserveStockItemService;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import com.example.eshop.warehouse.domain.StockQuantity;
import com.example.eshop.warehouse.messaging.ProcessedMessage;
import com.example.eshop.warehouse.messaging.ProcessedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Component
@Lazy(false)
@RequiredArgsConstructor
@Slf4j
public class ReserveStockForOrderEventListener {
    private final ReserveStockItemService reserveStockItemService;
    private final ProcessedMessageRepository processedMessageRepository;

    @KafkaListener(
            topics = CheckoutApi.RESERVE_STOCKS_TOPIC,
            containerFactory = "reserveStocksForOrderKafkaListenerContainerFactory"
    )
    @SendTo
    @Transactional(
            isolation = Isolation.REPEATABLE_READ // required for ReserveStockItemService
    )
    public ReservationResult handleEvent(@Payload OrderDto order, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        var processedMessage = processedMessageRepository.findByMessageKeyAndMessageClass(key, OrderDto.class);

        if (processedMessage.isPresent()) {
            return handleDuplicatedEvent(key, processedMessage.get());
        }

        return handleNewEvent(order, key);
    }

    /**
     * Handle stock reservation event that was processed before
     */
    private ReservationResult handleDuplicatedEvent(String key, ProcessedMessage processedMessage) {
        log.trace("Skip duplicated ReserveStockForOrderEvent: " + key);

        return (ReservationResult) processedMessage.getResult();
    }

    /**
     * Handle new stock reservation event
     */
    private ReservationResult handleNewEvent(OrderDto order, String key) {
        log.trace("Process ReserveStockForOrderEvent: " + order);

        var reservingItems = order.cart().getItems().stream()
                .collect(Collectors.toMap(CartItemDto::getEan, item -> StockQuantity.of(item.getQuantity())));
        var reservationResult = reserveStockItemService.reserve(reservingItems);

        processedMessageRepository.save(new ProcessedMessage(key, OrderDto.class, reservationResult));

        return reservationResult;
    }
}
