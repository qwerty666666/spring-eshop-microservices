package com.example.eshop.transactionaloutbox.messagerelay;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Message Relay for {@link TransactionalOutbox} which periodically
 * poll outbox and produce messages to broker by using
 * {@code brokerProducer}.
 * <p>
 * After producing messages they will be removed from the outbox.
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
public class SingleThreadedMessageRelay implements MessageRelay {
    private final TransactionalOutbox transactionalOutbox;
    private final BrokerProducer brokerProducer;
    private final int pollBatchSize;
    private final long pollPeriod;
    private final TimeUnit pollTimeUnit;
    private final ScheduledExecutorService executorService;
    private boolean isRunning = false;

    public SingleThreadedMessageRelay(
            TransactionalOutbox transactionalOutbox,
            BrokerProducer brokerProducer,
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

    @Override
    public void start() {
        if (isRunning) {
            return;
        }

        executorService.scheduleWithFixedDelay(this::publishMessages, 0, pollPeriod, pollTimeUnit);
        isRunning = true;
    }

    @Override
    public void shutdown() {
        if (!isRunning) {
            return;
        }

        executorService.shutdown();
        isRunning = false;
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
