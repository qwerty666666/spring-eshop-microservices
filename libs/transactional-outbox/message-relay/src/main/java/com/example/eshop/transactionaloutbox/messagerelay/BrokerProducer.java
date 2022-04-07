package com.example.eshop.transactionaloutbox.messagerelay;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import java.util.concurrent.CompletableFuture;

/**
 * Publish messages to Broker.
 * <p>
 * Should return List of successfully produced messages.
 */
public interface BrokerProducer {
    /**
     * Publish given message
     */
    CompletableFuture<?> process(OutboxMessage message);
}
