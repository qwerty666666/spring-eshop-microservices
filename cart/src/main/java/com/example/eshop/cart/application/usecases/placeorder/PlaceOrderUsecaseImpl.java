package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderValidator;
import com.example.eshop.sharedkernel.domain.Localizer;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.example.eshop.warehouse.client.reservationresult.InsufficientQuantityError;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import com.example.eshop.warehouse.client.reservationresult.StockItemNotFoundError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;
    private final OrderFactory orderFactory;
    private final Clock clock;
    private final ProductInfoProvider productInfoProvider;
    private final StockReservationService stockReservationService;
    private final Localizer localizer;

    @Override
    @PreAuthorize("#createOrderDto.customerId() == principal.getCustomerId()")
    @Transactional
    public Order place(CreateOrderDto createOrderDto) {
        // create Order
        var order = createOrder(createOrderDto);

        // check domain rules
        var result = placeOrderService.place(order);
        if (!result.isSuccess()) {
            throw new ValidationException(result.getErrors());
        }

        // reserve products in warehouse
        reserveStocksInWarehouse(order);

        // and publish Application Event
        publishOrderCreatedEvent(order);

        return order;
    }

    private Order createOrder(CreateOrderDto createOrderDto) {
        return orderFactory.create(createOrderDto);
    }

    private void reserveStocksInWarehouse(Order order) {
        var reservationResult = stockReservationService.reserve(order);

        if (!reservationResult.isSuccess()) {
            throw new ValidationException(buildReservationErrors(reservationResult));
        }
    }

    private Errors buildReservationErrors(ReservationResult reservationResult) {
        var errors = new Errors();

        reservationResult.getErrors().forEach((ean, reservationError) -> {
            String message;

            if (reservationError instanceof InsufficientQuantityError err) {
                message = localizer.getMessage("cart.item.insufficient_quantity", ean,
                        err.getReservingQuantity(), err.getAvailableQuantity());
            } else if (reservationError instanceof StockItemNotFoundError) {
                message = localizer.getMessage("cart.item.not_found", ean);
            } else {
                log.error("Unknown ReservationError type " + reservationResult.getClass());
                throw new StockReservationException("Unknown ReservationError type " + reservationResult.getClass());
            }

            errors.addError(PlaceOrderValidator.CART_ITEMS_FIELD, message);
        });

        return errors;
    }

    private void publishOrderCreatedEvent(Order order) {
        var creationDate = LocalDateTime.now(clock);
        var productsInfo = productInfoProvider.getProductsInfo(order.getCart());

        eventPublisher.publishEvent(new OrderPlacedEvent(order, creationDate, productsInfo));
    }
}
