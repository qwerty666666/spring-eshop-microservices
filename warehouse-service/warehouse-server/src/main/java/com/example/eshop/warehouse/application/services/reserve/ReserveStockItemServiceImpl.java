package com.example.eshop.warehouse.application.services.reserve;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import com.example.eshop.transactionaloutbox.outboxmessagefactory.DomainEventOutboxMessageFactory;
import com.example.eshop.warehouse.client.WarehouseApi;
import com.example.eshop.warehouse.client.events.ProductStockChangedEvent;
import com.example.eshop.warehouse.client.reservationresult.InsufficientQuantityError;
import com.example.eshop.warehouse.client.reservationresult.ReservationError;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import com.example.eshop.warehouse.client.reservationresult.StockItemNotFoundError;
import com.example.eshop.warehouse.domain.StockItem;
import com.example.eshop.warehouse.domain.StockItemRepository;
import com.example.eshop.warehouse.domain.StockQuantity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveStockItemServiceImpl implements ReserveStockItemService {
    private final StockItemRepository stockItemRepository;
    private final TransactionalOutbox transactionalOutbox;
    private final DomainEventOutboxMessageFactory outboxMessageFactory;

    @Override
    @Transactional(
            isolation = Isolation.REPEATABLE_READ // use REPEATABLE_READ to prevent lost updates
    )
    public ReservationResult reserve(Map<Ean, StockQuantity> reserveQuantity) {
        // find required StockItems
        var eanList = reserveQuantity.keySet();
        var stockItems = getStockItems(eanList);

        // try to reserve
        var reservationResult = reserve(reserveQuantity, stockItems);

        // rollback if there are any failed reservation
        if (!reservationResult.isSuccess()) {
            log.debug("Can't reserve stocks. Reason: " + reservationResult.getErrors());
            return reservationResult;
        }

        // publish StockItem Domain Events
        saveDomainEventsToOutbox(stockItems.values());

        // and return Success
        return ReservationResult.success();
    }

    private Map<Ean, StockItem> getStockItems(Collection<Ean> eanList) {
        return stockItemRepository.findByEanIn(eanList).stream()
                .collect(Collectors.toMap(StockItem::getEan, Function.identity()));
    }

    /**
     * Reserves all items or return failed reservation result if any
     * stock item can't be reserved.
     * <p>
     * It is guaranteed that either all items are reserved or none.
     */
    private ReservationResult reserve(Map<Ean, StockQuantity> reserveQuantity, Map<Ean, StockItem> stockItems) {
        var errors = checkIfItemsCanBeReserved(reserveQuantity, stockItems);

        if (errors.isEmpty()) {
            reserveQuantity.forEach((ean, qty) -> stockItems.get(ean).reserve(qty));

            return ReservationResult.success();
        }

        return ReservationResult.failure(errors);
    }

    /**
     * Checks if all items can be reserved and return errors if
     * any of the items can't.
     */
    private List<ReservationError> checkIfItemsCanBeReserved(Map<Ean, StockQuantity> reserveQuantity, Map<Ean, StockItem> stockItems) {
        var errors = new ArrayList<ReservationError>();

        reserveQuantity.forEach((ean, qty) -> {
            // check stock item existence
            if (!stockItems.containsKey(ean)) {
                errors.add(new StockItemNotFoundError(ean, "StockItem with EAN " + ean + " does not exist"));
                return;
            }

            // check that stock has enough quantity
            var item = stockItems.get(ean);
            if (!item.canReserve(qty)) {
                errors.add(new InsufficientQuantityError(ean, qty.toInt(), item.getStockQuantity().toInt(),
                        "There is no enough quantity to reserve %s. Required quantity - %s, but only %s items available."
                                .formatted(item.getEan(), qty.toInt(), item.getStockQuantity().toInt()))
                );
            }
        });

        return errors;
    }

    /**
     * Saved domain events from {@link StockItem}s to {@link TransactionalOutbox}
     */
    private void saveDomainEventsToOutbox(Collection<StockItem> stockItems) {
        var stockChangedMessages = new ArrayList<OutboxMessage>();

        for (var item: stockItems) {
            for (var event: item.getDomainEventsAndRemove(ProductStockChangedEvent.class)) {
                var message = outboxMessageFactory.create(WarehouseApi.STOCK_CHANGED_TOPIC, event, item);
                stockChangedMessages.add(message);
            }
        }

        transactionalOutbox.add(stockChangedMessages);
    }
}
