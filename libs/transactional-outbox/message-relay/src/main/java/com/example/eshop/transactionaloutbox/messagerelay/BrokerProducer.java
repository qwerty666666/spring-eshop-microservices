package com.example.eshop.transactionaloutbox.messagerelay;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Publish messages to Broker.
 * <p>
 * Should return List of successfully produced messages.
 */
public interface BrokerProducer extends UnaryOperator<List<OutboxMessage>> {
}
