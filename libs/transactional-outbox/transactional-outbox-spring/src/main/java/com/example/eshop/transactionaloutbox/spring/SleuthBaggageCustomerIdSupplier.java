package com.example.eshop.transactionaloutbox.spring;

import com.example.eshop.distributedtracing.BaggageFields;
import com.example.eshop.transactionaloutbox.outboxmessagefactory.CustomerIdSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.sleuth.BaggageInScope;
import org.springframework.cloud.sleuth.Tracer;
import java.util.Optional;

/**
 * Supply customer ID from {@link Tracer} baggage "customer-id".
 */
@RequiredArgsConstructor
public class SleuthBaggageCustomerIdSupplier implements CustomerIdSupplier {
    private static final String BAGGAGE_NAME = BaggageFields.CUSTOMER_ID.name();

    private final Tracer tracer;

    @Override
    public String get() {
        return Optional.ofNullable(tracer.getBaggage(BAGGAGE_NAME))
                .map(BaggageInScope::get)
                .orElse("");
    }
}
