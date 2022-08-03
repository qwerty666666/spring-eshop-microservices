package com.example.eshop.transactionaloutbox;

import java.util.List;

/**
 * Storage for {@link OutboxMessage}s.
 * <p>
 * It works as a queue and return Messages in FIFO order.
 * <p>
 * If the storage supports concurrent message handling is
 * specific implementation details.
 */
public interface TransactionalOutbox {
    /**
     * Enqueue new message
     */
    default void add(OutboxMessage message) {
        add(List.of(message));
    }

    /**
     * Enqueue new messages
     */
    void add(List<OutboxMessage> messages);

    /**
     * Returns first {@code limit} messages from queue
     */
    List<OutboxMessage> getMessages(int limit);

    /**
     * Removes given messages from queue
     */
    void remove(List<OutboxMessage> messages);

    /**
     * Removes given message from queue
     */
    default void remove(OutboxMessage message) {
        remove(List.of(message));
    }
}
