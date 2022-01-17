package com.example.eshop.warehouse.application.eventlisteners;

import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.order.CartItemDto;
import com.example.eshop.checkout.client.order.OrderDto;
import com.example.eshop.warehouse.application.services.reserve.ReserveStockItemService;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import com.example.eshop.warehouse.domain.StockQuantity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
@Lazy(false)
@RequiredArgsConstructor
public class ReserveStockForOrderEventListeners {
    private final ReserveStockItemService reserveStockItemService;

    @KafkaListener(
            topics = CheckoutApi.RESERVE_STOCKS_TOPIC,
            containerFactory = "reserveStocksForOrderKafkaListenerContainerFactory"
    )
    @SendTo
    public ReservationResult reserve(OrderDto order) {
        var reservingItems = order.cart().items().stream()
                .collect(Collectors.toMap(CartItemDto::ean, item -> StockQuantity.of(item.quantity())));

        return reserveStockItemService.reserve(reservingItems);
    }
}
