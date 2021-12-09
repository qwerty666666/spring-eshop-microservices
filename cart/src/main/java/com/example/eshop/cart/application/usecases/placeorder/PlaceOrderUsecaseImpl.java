package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.example.eshop.warehouse.application.services.reserve.ReserveStockItemService;
import com.example.eshop.warehouse.domain.StockQuantity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceOrderUsecaseImpl implements PlaceOrderUsecase {
    private final PlaceOrderService placeOrderService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderFactory orderFactory;
    private final Clock clock;
    private final ProductInfoProvider productInfoProvider;
    private final ReserveStockItemService reserveStockItemService;

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
        var reservingQty = order.getCart().getItems().stream()
                .collect(Collectors.toMap(CartItem::getEan, item -> StockQuantity.of(item.getQuantity())));

        reserveStockItemService.reserve(reservingQty);
    }

    private void publishOrderCreatedEvent(Order order) {
        var creationDate = LocalDateTime.now(clock);
        var productsInfo = productInfoProvider.getProductsInfo(order.getCart());

        eventPublisher.publishEvent(new OrderPlacedEvent(order, creationDate, productsInfo));
    }
}
