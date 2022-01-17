package com.example.eshop.warehouse.client.reservationresult;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Result of reserve StockItem operation.
 */
@EqualsAndHashCode
public class ReservationResult {
    private final Map<Ean, ReservationError> errors;

    @JsonCreator
    private ReservationResult(@JsonProperty("errors") Map<Ean, ReservationError> errors) {
        this.errors = errors;
    }

    /**
     * @return new Success Result
     */
    public static ReservationResult success() {
        return new ReservationResult(Collections.emptyMap());
    }

    /**
     * @return new Failure Result
     */
    public static ReservationResult failure(List<ReservationError> errors) {
        return new ReservationResult(
                errors.stream().collect(Collectors.toMap(ReservationError::getEan, Function.identity()))
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
    public Map<Ean, ReservationError> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

}
