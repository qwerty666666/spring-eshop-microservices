package com.example.eshop.warehouse.application.services.reserve;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.warehouse.application.services.reserve.ReserveResult.InsufficientQuantityError;
import com.example.eshop.warehouse.application.services.reserve.ReserveResult.ReservingError;
import com.example.eshop.warehouse.application.services.reserve.ReserveResult.StockItemNotFoundError;
import com.example.eshop.warehouse.domain.InsufficientStockQuantityException;
import com.example.eshop.warehouse.domain.StockItem;
import com.example.eshop.warehouse.domain.StockItemRepository;
import com.example.eshop.warehouse.domain.StockQuantity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReserveStockItemServiceImpl implements ReserveStockItemService {
    private final StockItemRepository stockItemRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PlatformTransactionManager txManager;

    @Override
    public ReserveResult reserve(Map<Ean, StockQuantity> reserveQuantity) {
        return new TransactionTemplate(txManager).execute(status -> {
            // find required StockItems
            var stockItems = getStockItems(reserveQuantity.keySet());

            // try to reserve
            var errors = reserve(reserveQuantity, stockItems);

            // check if there are any failed reservation
            if (!errors.isEmpty()) {
                status.setRollbackOnly();
                return ReserveResult.failure(errors);
            }

            // publish StockItem Domain Events
            publishDomainEvents(stockItems.values());

            // and return Success
            return ReserveResult.success();
        });
    }

    private Map<Ean, StockItem> getStockItems(Collection<Ean> eanList) {
        return stockItemRepository.findByEanIn(eanList).stream()
                .collect(Collectors.toMap(StockItem::getEan, Function.identity()));
    }

    private List<ReservingError> reserve(Map<Ean, StockQuantity> reserveQuantity, Map<Ean, StockItem> stockItems) {
        var errors = new ArrayList<ReservingError>();

        reserveQuantity.forEach((ean, qty) -> {
            if (!stockItems.containsKey(ean)) {
                errors.add(new StockItemNotFoundError(ean, "StockItem with EAN " + ean + " does not exist"));
                return;
            }

            var item = stockItems.get(ean);

            try {
                item.reserve(qty);
            } catch (InsufficientStockQuantityException e) {
                errors.add(new InsufficientQuantityError(item, qty, "Can't reserve " + qty + " for " + item));
            }
        });

        return errors;
    }

    private void publishDomainEvents(Collection<StockItem> stockItems) {
        stockItems.stream()
                .flatMap(item -> item.getDomainEventsAndClear().stream())
                .forEach(eventPublisher::publishEvent);
    }
}
