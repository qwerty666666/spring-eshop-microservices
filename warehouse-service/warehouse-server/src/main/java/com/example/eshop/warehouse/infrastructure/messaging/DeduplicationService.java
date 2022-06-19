package com.example.eshop.warehouse.infrastructure.messaging;

import java.util.function.BiFunction;

public interface DeduplicationService {
    /**
     * Allows to process message only once.
     * <p>
     * Delegates to the {@code messageHandler} if message with the given {@code messageKey}
     * has not been processed before, otherwise returns the saved result of processing
     * the given {@code message}.
     *
     * @param <M> message type
     * @param <K> type of the message's key
     * @param <R> handler result type
     */
    <M, K, R> R deduplicate(M message, K messageKey, BiFunction<M, K, R> messageHandler);
}
