package com.example.eshop.transactionaloutbox.messagerelay;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

/**
 * Message Relay for {@link TransactionalOutbox} which periodically
 * poll outbox and produce messages to broker by using
 * {@code brokerProducer}.
 * <p>
 * {@code brokerProducer} should return List of successfully produced
 * messages synchronously. After returning produced messages they will
 * be removed from the outbox.
 * <p>
 * This class supports batch processing. It takes {@code pollBatchSize}
 * messages from outbox and publish them, until there is no more than
 * {@code pollBatchSize} messages in the outbox. After that the relay
 * will be paused for {@code pollPeriod} time.
 * <p>
 * This service is single-threaded as we should support message ordering.
 * If you need to scale Message Relay, you should provide
 * {@code transactionalOutbox} with concurrent polling support.
 */
public class SingleThreadedMessageRelay {
    private final TransactionalOutbox transactionalOutbox;
    private final UnaryOperator<List<OutboxMessage>> brokerProducer;
    private final int pollBatchSize;
    private final long pollPeriod;
    private final TimeUnit pollTimeUnit;
    private final ScheduledExecutorService executorService;

    public SingleThreadedMessageRelay(
            TransactionalOutbox transactionalOutbox,
            UnaryOperator<List<OutboxMessage>> brokerProducer,
            int pollBatchSize,
            long pollPeriod,
            TimeUnit pollTimeUnit) {
        this.transactionalOutbox = transactionalOutbox;
        this.pollBatchSize = pollBatchSize;
        this.brokerProducer = brokerProducer;
        this.pollPeriod = pollPeriod;
        this.pollTimeUnit = pollTimeUnit;

        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Starts polling outbox and producing messages
     */
    public void start() {
        executorService.scheduleWithFixedDelay(this::publishMessages, 0, pollPeriod, pollTimeUnit);
    }

    /**
     * Stops message relay.
     * <p>
     * The guarantees of handling in-process messages are the same
     * as {@link ExecutorService#shutdown()} has.
     */
    public void stop() {
        executorService.shutdown();
    }

    private void publishMessages() {
        List<OutboxMessage> newMessages;

        do {
            newMessages = transactionalOutbox.getMessages(pollBatchSize);

            var publishedMessages = brokerProducer.apply(newMessages);

            transactionalOutbox.remove(publishedMessages);
        } while (newMessages.size() >= pollBatchSize);
    }
}
