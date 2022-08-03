package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.transactionaloutbox.OutboxMessage;

/**
 * Supplies {@link OutboxMessage#getRequestId()} for current request context.
 */
public interface RequestIdSupplier {
    /**
     * @return request ID for current context
     */
    String get();
}
