package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderValidator;
import com.example.eshop.catalog.client.CatalogService;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.example.eshop.warehouse.client.reservationresult.InsufficientQuantityError;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import com.example.eshop.warehouse.client.reservationresult.StockItemNotFoundError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceOrderUsecaseImpl implements PlaceOrderUsecase {
    private final PlaceOrderService placeOrderService;
    private final OrderPlacedEventPublisher orderPlacedEventPublisher;
    private final OrderFactory orderFactory;
    private final Clock clock;
    private final CatalogService catalogService;
    private final StockReservationService stockReservationService;
    private final OrderMapper orderMapper;

    @Override
    @PreAuthorize("#createOrderDto.customerId() == authentication.getCustomerId()")
    @Transactional
    public Order place(CreateOrderDto createOrderDto) {
        // create Order
        var order = createOrder(createOrderDto);

        // check domain rules
        var result = placeOrderService.place(order);
        if (!result.isSuccess()) {
            throw new ValidationException(result.getErrors());
        }

        var orderDto = createOrderDto(order);

        // reserve products in warehouse
        reserveStocksInWarehouse(orderDto);

        // and publish Application Event
        publishOrderCreatedEvent(orderDto);

        return order;
    }

    private Order createOrder(CreateOrderDto createOrderDto) {
        return orderFactory.create(createOrderDto);
    }

    private OrderDto createOrderDto(Order order) {
        var eanList = order.getCart().getItems().stream().map(CartItem::getEan).toList();
        var skuInfo = catalogService.getSku(eanList)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("CatalogService::getSku() return null"));

        return orderMapper.toOrderDto(order, skuInfo);
    }

    private void reserveStocksInWarehouse(OrderDto order) {
        var reservationResult = stockReservationService.reserve(order);

        if (!reservationResult.isSuccess()) {
            throw new ValidationException(buildReservationErrors(reservationResult));
        }
    }

    private Errors buildReservationErrors(ReservationResult reservationResult) {
        var errors = new Errors();

        reservationResult.getErrors().forEach((ean, reservationError) -> {
            if (reservationError instanceof InsufficientQuantityError err) {
                errors.addError(PlaceOrderValidator.CART_ITEMS_FIELD, "cart.item.insufficient_quantity",
                        ean, err.getReservingQuantity(), err.getAvailableQuantity());
            } else if (reservationError instanceof StockItemNotFoundError) {
                errors.addError(PlaceOrderValidator.CART_ITEMS_FIELD, "cart.item.not_found", ean);
            } else {
                log.error("Unknown ReservationError type " + reservationResult.getClass());
                throw new PublishEventException("Unknown ReservationError type " + reservationResult.getClass());
            }
        });

        return errors;
    }

    private void publishOrderCreatedEvent(OrderDto order) {
        var creationDate = LocalDateTime.now(clock);
        var event = new OrderPlacedEvent(order, creationDate);

        orderPlacedEventPublisher.publish(event);
    }
}
