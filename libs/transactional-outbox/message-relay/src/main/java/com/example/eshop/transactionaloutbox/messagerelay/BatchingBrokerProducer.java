package com.example.eshop.transactionaloutbox.messagerelay;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * Synchronously publish batch of {@link OutboxMessage}
 */
public class BatchingBrokerProducer {
    private final BrokerProducer brokerProducer;

    public BatchingBrokerProducer(BrokerProducer brokerProducer) {
        Objects.requireNonNull(brokerProducer);

        this.brokerProducer = brokerProducer;
    }

    /**
     * Publish given messages
     *
     * @return list of successfully published messages
     */
    public List<OutboxMessage> process(List<OutboxMessage> messages) {
        var futures = messages.stream()
                .map(brokerProducer::process)
                .toList();

        try {
            // wait for all messages to be published
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        } catch (Exception ignored) {
            // exception is thrown if any of futures failed
        }

        return IntStream.range(0, messages.size())
                .filter(i -> !futures.get(i).isCompletedExceptionally())
                .mapToObj(messages::get)
                .toList();
    }
}
