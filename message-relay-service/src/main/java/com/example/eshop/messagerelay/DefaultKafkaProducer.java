package com.example.eshop.messagerelay;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.messagerelay.BrokerProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * Give all messages to Kafka Client.
 *
 * Kafka client can batch messages before send them to broker,
 * and client will wait for inger.ms if the buffer is not full.
 *
 * There is an order guarantee between messages addressed to the
 * same partition. But between different partitions, there is can
 * be that some messages from the batch are not delivered, while
 * others are delivered successfully. Therefore, this implementation
 * can't be used if we should preserve order as in underlying
 * outbox, or we should produce single messages without batching.
 */
@Component
@RequiredArgsConstructor
public class DefaultKafkaProducer implements BrokerProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Override
    public List<OutboxMessage> apply(List<OutboxMessage> messages) {
        var futures = messages.stream()
                .map(message -> kafkaTemplate.send(message.getTopic(), message.getPayload()).completable())
                .toList();

        try {
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
