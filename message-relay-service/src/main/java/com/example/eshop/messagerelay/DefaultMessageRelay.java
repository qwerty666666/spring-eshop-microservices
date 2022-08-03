package com.example.eshop.messagerelay;

import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import com.example.eshop.transactionaloutbox.messagerelay.BrokerProducer;
import com.example.eshop.transactionaloutbox.messagerelay.MessageRelay;
import com.example.eshop.transactionaloutbox.messagerelay.SingleThreadedMessageRelay;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Message relay that polls messages from the given {@link TransactionalOutbox}
 * and produce them to {@link BrokerProducer}.
 * <p>
 * This class is a wrapper around {@link SingleThreadedMessageRelay}. We use this
 * class instead of use {@link SingleThreadedMessageRelay} directly because we
 * should shutdown message relay when Spring's Context is closed.
 */
@Slf4j
public class DefaultMessageRelay implements MessageRelay {
    private static final int DEFAULT_POLL_BATCH_SIZE = 5;
    private static final int DEFAULT_POLL_PERIOD = 5;
    private static final TimeUnit DEFAULT_POLL_PERIOD_TIME_UNIT = TimeUnit.SECONDS;

    private final MessageRelay delegate;

    public DefaultMessageRelay(String serviceName, TransactionalOutbox outbox, BrokerProducer brokerProducer) {
        this(serviceName, outbox, brokerProducer, DEFAULT_POLL_BATCH_SIZE, DEFAULT_POLL_PERIOD,
                DEFAULT_POLL_PERIOD_TIME_UNIT);
    }

    public DefaultMessageRelay(String serviceName, TransactionalOutbox outbox, BrokerProducer brokerProducer,
            int pollBatchSize, int pollPeriod, TimeUnit pollTimeUnit) {
        delegate = new SingleThreadedMessageRelay(
                serviceName,
                outbox,
                brokerProducer,
                pollBatchSize,
                pollPeriod,
                pollTimeUnit
        );
    }

    @Override
    public void start() {
        delegate.start();
    }

    @Override
    @PreDestroy
    public void shutdown() {
        delegate.shutdown();
    }
}
