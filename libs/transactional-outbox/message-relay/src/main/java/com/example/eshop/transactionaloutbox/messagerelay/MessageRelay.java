package com.example.eshop.transactionaloutbox.messagerelay;

import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import java.util.concurrent.ExecutorService;

/**
 * Message Relay for {@link TransactionalOutbox} which periodically
 * poll outbox and produce messages to broker.
 * <p>
 * Implementations are responsible for async, concurrent message
 * handling and delivery semantic.
 */
public interface MessageRelay {
    /**
     * Starts polling outbox and producing messages
     */
    void start();

    /**
     * Stops message relay.
     * <p>
     * The guarantees of handling in-process messages are the same
     * as {@link ExecutorService#shutdown()} has.
     */
    void shutdown();
}
