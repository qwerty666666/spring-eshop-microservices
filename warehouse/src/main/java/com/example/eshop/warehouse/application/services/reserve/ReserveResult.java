package com.example.eshop.warehouse.application.services.reserve;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.warehouse.domain.StockItem;
import com.example.eshop.warehouse.domain.StockQuantity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Result of reserve StockItem operation.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReserveResult {
    private final Map<Ean, ReservingError> errors;

    /**
     * @return new Success Result
     */
    public static ReserveResult success() {
        return new ReserveResult(Collections.emptyMap());
    }

    /**
     * @return new Failure Result
     */
    public static ReserveResult failure(List<ReservingError> errors) {
        return new ReserveResult(
                errors.stream().collect(Collectors.toMap(ReservingError::getEan, Function.identity()))
        );
    }

    /**
     * @return if all items was reserved successfully
     */
    public boolean isSuccess() {
        return errors.isEmpty();
    }

    /**
     * @return errors for StockItems that failed to reserve
     */
    public Map<Ean, ReservingError> getErrors() {
        return errors;
    }

    /**
     * Error for reserving single {@link StockItem}
     */
    @RequiredArgsConstructor
    @Getter
    public abstract static class ReservingError {
        protected final String message;
        protected final Ean ean;
    }

    /**
     * Error indicating that StockItem with given EAN does not
     * exist in warehouse.
     */
    public static class StockItemNotFoundError extends ReservingError {
        public StockItemNotFoundError(Ean ean, String message) {
            super(message, ean);
        }
    }

    /**
     * Error indicating that requested {@code reservingQuantity}
     * can't be reserved, because there is no enough quantity for
     * the given StockItem.
     */
    @Getter
    public static class InsufficientQuantityError extends ReservingError {
        private final StockItem item;
        private final StockQuantity reservingQuantity;

        public InsufficientQuantityError(StockItem item, StockQuantity reservingQuantity, String message) {
            super(message, item.getEan());
            this.item = item;
            this.reservingQuantity = reservingQuantity;
        }

        /**
         * @return StockItem's available quantity
         */
        public StockQuantity getAvailableQuantity() {
            return item.getStockQuantity();
        }
    }
}
