package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.transactionaloutbox.OutboxMessage;
import java.util.function.Function;

/**
 * Serialize {@link DomainEvent} to {@link OutboxMessage#getPayload()}
 */
public interface EventSerializer extends Function<DomainEvent, byte[]> {
}
