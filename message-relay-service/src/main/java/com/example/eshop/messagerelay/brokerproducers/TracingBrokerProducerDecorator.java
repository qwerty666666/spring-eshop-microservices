package com.example.eshop.messagerelay.brokerproducers;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.messagerelay.BrokerProducer;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.propagation.Propagator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * {@link BrokerProducer} decorator that starts new {@link Span}
 * specified in {@link OutboxMessage} before publish the message
 * to broker.
 */
public class TracingBrokerProducerDecorator implements BrokerProducer {
    private static final String SPAN_NAME = "message-relay";
    private static final String MESSAGE_ID_TAG = "message.id";

    private final BrokerProducer delegate;
    private final Tracer tracer;
    private final Propagator propagator;

    public TracingBrokerProducerDecorator(BrokerProducer delegate, Tracer tracer, Propagator propagator) {
        this.delegate = Objects.requireNonNull(delegate);
        this.tracer = Objects.requireNonNull(tracer);
        this.propagator = Objects.requireNonNull(propagator);
    }

    @Override
    public CompletableFuture<?> process(OutboxMessage message) {
        var span = createSpan(message);

        if (span.isPresent()) {
            return processInNewSpan(message, span.get());
        }

        return processWithoutSpan(message);
    }

    /**
     * @return new span created from requestId or null if requestId from is invalid
     */
    private Optional<Span> createSpan(OutboxMessage message) {
        return extractRequestId(message)
                .map(requestId -> propagator.extract(requestId, (carrier, key) -> carrier)
                        .name(SPAN_NAME)
                        .tag(MESSAGE_ID_TAG, message.getId().toString())
                        .start()
                );
    }

    /**
     * Extract request ID from given {@link OutboxMessage}. The returned value can be
     * in unknown format.
     */
    private Optional<String> extractRequestId(OutboxMessage message) {
        var requestId = message.getRequestId();

        if (requestId == null || requestId.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(requestId);
    }

    /**
     * Process {@link OutboxMessage} in new {@link Span}
     */
    private CompletableFuture<?> processInNewSpan(OutboxMessage message, Span span) {
        try (var spanInScope = tracer.withSpan(span)) {
            return delegate.process(message);
        } finally {
            span.end();
        }
    }

    private CompletableFuture<?> processWithoutSpan(OutboxMessage message) {
        return delegate.process(message);
    }
}
