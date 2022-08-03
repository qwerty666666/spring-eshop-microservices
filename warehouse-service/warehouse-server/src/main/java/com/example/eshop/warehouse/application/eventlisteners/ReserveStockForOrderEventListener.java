package com.example.eshop.warehouse.application.eventlisteners;

import com.example.eshop.cart.client.model.CartItemDto;
import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.warehouse.application.services.reserve.ReserveStockItemService;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import com.example.eshop.warehouse.domain.StockQuantity;
import com.example.eshop.warehouse.infrastructure.messaging.DeduplicationService;
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

/**
 * Reserve stock items Event Handler.
 */
@Component
@Lazy(false)
@RequiredArgsConstructor
@Slf4j
public class ReserveStockForOrderEventListener {
    private final ReserveStockItemService reserveStockItemService;
    private final DeduplicationService deduplicationService;

    @KafkaListener(
            topics = CheckoutApi.RESERVE_STOCKS_TOPIC,
            containerFactory = "reserveStocksForOrderKafkaListenerContainerFactory"
    )
    @SendTo
    @Transactional(
            isolation = Isolation.REPEATABLE_READ // required for ReserveStockItemService
    )
    public ReservationResult onEvent(@Payload OrderDto order, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        return deduplicationService.deduplicate(order, key, this::handleEvent);
    }

    private ReservationResult handleEvent(OrderDto order, String key) {
        var reservingItems = order.cart().getItems().stream()
                .collect(Collectors.toMap(CartItemDto::getEan, item -> StockQuantity.of(item.getQuantity())));

        return reserveStockItemService.reserve(reservingItems);
    }
}
