package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import java.util.function.Supplier;

/**
 * Supplies {@link OutboxMessage#getRequestId()} for current request context.
 */
public interface RequestIdSupplier extends Supplier<String> {
}
