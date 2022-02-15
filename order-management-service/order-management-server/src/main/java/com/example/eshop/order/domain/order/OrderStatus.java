package com.example.eshop.order.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
    /**
     * It is the only Status we use. This Status is applied to order
     * after its creation.
     */
    PENDING("PENDING", "order_status.pending");

    private final String code;
    /**
     * Message code for l10n
     */
    private final String messageCode;
}
