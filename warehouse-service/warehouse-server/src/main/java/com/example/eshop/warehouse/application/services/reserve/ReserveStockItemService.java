package com.example.eshop.warehouse.application.services.reserve;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import com.example.eshop.warehouse.domain.StockItem;
import com.example.eshop.warehouse.domain.StockQuantity;
import java.util.Map;

/**
 * Application Service that reserve {@link StockItem} in warehouse.
 */
public interface ReserveStockItemService {
    /**
     * Reserve quantity for the given {@link StockItem}s
     */
    ReservationResult reserve(Map<Ean, StockQuantity> reserveQuantity);
}
