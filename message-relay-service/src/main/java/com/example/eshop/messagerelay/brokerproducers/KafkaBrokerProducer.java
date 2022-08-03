package com.example.eshop.messagerelay.brokerproducers;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.messagerelay.BrokerProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.mapping.AbstractJavaTypeMapper;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Produce all messages to Kafka.
 *
 * Kafka client can batch messages before send them to broker,
 * and client will wait for inger.ms if the buffer is not full.
 *
 * There is an order guarantee between messages addressed to the
 * same partition. But between different partitions, there is can
 * be that some messages from the batch are not delivered, while
 * others are delivered successfully. Therefore, this implementation
 * can't be used if we should preserve order the same as in underlying
 * outbox, or we should produce single messages without batching.
 */
public class KafkaBrokerProducer implements BrokerProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaBrokerProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        Objects.requireNonNull(kafkaTemplate, "kafkaTemplate is required");

        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public CompletableFuture<?> process(OutboxMessage message) {
        return kafkaTemplate.send(createRecord(message)).completable();
    }

    private ProducerRecord<String, byte[]> createRecord(OutboxMessage message) {
        var record = new ProducerRecord<String, byte[]>(message.getTopic(), message.getPayload());

        var contentClass = Optional.ofNullable(message.getType())
                .map(type -> type.getBytes(StandardCharsets.UTF_8))
                .orElse(null);
        record.headers().add(AbstractJavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME, contentClass);

        return record;
    }
}
