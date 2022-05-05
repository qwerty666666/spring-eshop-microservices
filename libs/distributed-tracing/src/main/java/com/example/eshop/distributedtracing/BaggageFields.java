package com.example.eshop.distributedtracing;

import brave.baggage.BaggageField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.cloud.sleuth.BaggageInScope;

/**
 * List of used {@link BaggageInScope}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BaggageFields {
    public static final BaggageField CUSTOMER_ID = BaggageField.create("customer-id");
}
