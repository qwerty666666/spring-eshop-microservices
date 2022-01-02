package com.example.eshop.transactionaloutbox;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.time.Instant;

/**
 * Single Message stored in TransactionalOutbox.
 */
@Getter
public class OutboxMessage {
    private final Integer id;

    private final String aggregate;
    private final String aggregateId;

    private final String type;

    private final String topic;
    private final byte[] payload;

    private final String requestId;
    private final Instant creationTime;

    public OutboxMessage(Integer id, String aggregate, String aggregateId, String type, String topic, byte[] payload, String requestId, Instant creationTime) {
        this.id = id;
        this.aggregate = aggregate;
        this.aggregateId = aggregateId;
        this.type = type;
        this.topic = topic;
        this.payload = payload;
        this.requestId = requestId;
        this.creationTime = creationTime;
    }

    private OutboxMessage(OutboxMessageBuilder builder) {
        this(null, builder.aggregate, builder.aggregateId, builder.type, builder.topic, builder.payload, builder.requestId, Instant.now());
    }

    public static TopicStep builder() {
        return new OutboxMessageBuilder();
    }

    public interface TopicStep {
        PayloadStep topic(String topic);
    }

    public interface PayloadStep {
        OutboxMessageBuilder payload(byte[] payload);
    }
    
    @Accessors(fluent = true)
    public static class OutboxMessageBuilder implements TopicStep, PayloadStep {
        private String topic;
        private byte[] payload;
        @Setter
        private String aggregate;
        @Setter
        private String aggregateId;
        @Setter
        private String type;
        @Setter
        private String requestId;

        public OutboxMessage build() {
            return new OutboxMessage(this);
        }

        @Override
        public PayloadStep topic(String topic) {
            if (topic == null || topic.isBlank()) {
                throw new IllegalArgumentException("Topic is required");
            }

            this.topic = topic;

            return this;
        }

        @Override
        public OutboxMessageBuilder payload(byte[] payload) {
            this.payload = payload;

            return this;
        }
    }
}
