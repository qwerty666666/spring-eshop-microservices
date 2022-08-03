package com.example.eshop.transactionaloutbox.messagerelay;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Objects;
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
@Slf4j
public class SingleThreadedMessageRelay implements MessageRelay {
    private final String name;
    private final TransactionalOutbox transactionalOutbox;
    private final BatchingBrokerProducer brokerProducer;
    private final int pollBatchSize;
    private final long pollPeriod;
    private final TimeUnit pollTimeUnit;
    private final ScheduledExecutorService executorService;
    private boolean isRunning = false;

    public SingleThreadedMessageRelay(
            String name,
            TransactionalOutbox transactionalOutbox,
            BrokerProducer brokerProducer,
            int pollBatchSize,
            long pollPeriod,
            TimeUnit pollTimeUnit) {
        Objects.requireNonNull(transactionalOutbox);
        Objects.requireNonNull(brokerProducer);
        if (pollBatchSize <= 0) {
            throw new IllegalArgumentException("pollBatchSize should be positive");
        }
        if (pollPeriod <= 0) {
            throw new IllegalArgumentException("pollPeriod should be positive");
        }

        this.name = name;
        this.transactionalOutbox = transactionalOutbox;
        this.pollBatchSize = pollBatchSize;
        this.brokerProducer = new BatchingBrokerProducer(brokerProducer);
        this.pollPeriod = pollPeriod;
        this.pollTimeUnit = pollTimeUnit;

        executorService = Executors.newSingleThreadScheduledExecutor(r ->
                new Thread(r, "SingleThreadedMessageRelay-" + name)
        );
    }

    @Override
    public void start() {
        if (isRunning) {
            return;
        }

        log.info("Start Message Relay '%s'".formatted(name));

        executorService.scheduleWithFixedDelay(decorateTask(this::publishMessages), 0, pollPeriod, pollTimeUnit);
        isRunning = true;
    }

    @Override
    public void shutdown() {
        if (!isRunning) {
            return;
        }

        log.info("Stop Message Relay '%s'".formatted(name));

        executorService.shutdown();
        isRunning = false;

        log.info("Message Relay '%s' stopped".formatted(name));
    }

    private void publishMessages() {
        List<OutboxMessage> newMessages;

        do {
            newMessages = transactionalOutbox.getMessages(pollBatchSize);

            if (log.isDebugEnabled()) {
                log.debug("Polled " + newMessages.size() + " new messages");
            }

            var publishedMessages = brokerProducer.process(newMessages);

            if (log.isDebugEnabled()) {
                log.debug(publishedMessages.size() + " was published to message broker");
            }

            // TODO handle messages that can't be published
            // otherwise we will handle the same invalid messages all the time

            transactionalOutbox.remove(publishedMessages);
        } while (newMessages.size() >= pollBatchSize);
    }

    /**
     * We need to wrap task scheduled by executor service in try-catch block
     * because otherwise task fail will halt the executor service silently.
     */
    private Runnable decorateTask(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("message relay " + name + " failed with exception", e);
            }
        };
    }
}
