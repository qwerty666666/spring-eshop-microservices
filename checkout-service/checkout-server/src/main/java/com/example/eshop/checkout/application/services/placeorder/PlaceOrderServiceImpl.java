package com.example.eshop.checkout.application.services.placeorder;

import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.checkout.application.services.placeorder.stockreservation.StockReservationService;
import com.example.eshop.checkout.application.services.OrderFactory;
import com.example.eshop.checkout.application.services.PublishEventException;
import com.example.eshop.checkout.application.services.placeorder.orderplacedevent.OrderPlacedEventPublisher;
import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.checkout.domain.placeorder.PlaceOrderValidator;
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
import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceOrderServiceImpl implements PlaceOrderService {
    private final com.example.eshop.checkout.domain.placeorder.PlaceOrderService placeOrderDomainService;
    private final OrderFactory orderFactory;
    private final OrderMapper orderMapper;
    private final OrderPlacedEventPublisher orderPlacedEventPublisher;
    private final StockReservationService stockReservationService;
    private final Clock clock;

    @Override
    @PreAuthorize("#createOrderDto.customerId() == authentication.getCustomerId()")
    public Order place(CreateOrderDto createOrderDto) {
        // create Order
        var order = orderFactory.create(createOrderDto);

        // check domain rules
        var result = placeOrderDomainService.place(order);
        if (!result.isSuccess()) {
            throw new ValidationException(result.getErrors());
        }

        // publish Application events
        firePlaceOrderEvents(order);

        return order;
    }

    private void firePlaceOrderEvents(Order order) {
        var orderDto = orderMapper.toOrderDto(order);

        reserveStocksInWarehouse(orderDto);
        publishOrderPlacedEvent(orderDto);
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

    private void publishOrderPlacedEvent(OrderDto order) {
        var creationDate = LocalDateTime.now(clock);
        var event = new OrderPlacedEvent(order, creationDate);

        orderPlacedEventPublisher.publish(event);
    }
}
