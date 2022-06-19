package com.example.eshop.warehouse.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Jpa implementation of {@link DeduplicationService}. It saves processed messages
 * as {@link ProcessedMessage}.
 * <p>
 * This service MUST be called within the same transaction as the messageHandler.
 * This class checks if it is called within opened transaction and fails if transaction
 * wasn't open.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JpaDeduplicationService implements DeduplicationService {
    private final ProcessedMessageRepository processedMessageRepository;

    @Override
    public <M, K, R> R deduplicate(M message, K messageKey, BiFunction<M, K, R> messageHandler) {
        ensureThatCalledWithinTransaction();

        return (R) findMessage(message, messageKey)
                .map(this::handleExistedMassage)
                .orElseGet(() -> handleNewMessage(message, messageKey, messageHandler))
                .getResult();
    }

    /**
     * Throws exception if this method called outside of transaction.
     */
    private void ensureThatCalledWithinTransaction() {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("No currently active transaction detected." + this.getClass() +
                    " methods MUST be called within transaction.");
        }
    }

    /**
     * Returns saved message result if it was processed before.
     */
    private <M, K> Optional<ProcessedMessage> findMessage(M message, K messageKey) {
        return processedMessageRepository.findByMessageKeyAndMessageClass(messageKey.toString(), message.getClass());
    }

    /**
     * Returns result of the processed message.
     */
    private ProcessedMessage handleExistedMassage(ProcessedMessage message) {
        log.trace("Skip duplicated message: " + message.getMessageKey());

        return message;
    }

    /**
     * Delegates to the {@code messageHandler} and saves result to DB.
     */
    private <M, K, R> ProcessedMessage handleNewMessage(M message, K messageKey, BiFunction<M, K, R> messageHandler) {
        log.trace("Process new message: " + messageKey);

        var result = messageHandler.apply(message, messageKey);

       return save(message, messageKey, result);
    }

    /**
     * Saves the given message to DB as {@link ProcessedMessage} and returns it.
     */
    private <M, K, R> ProcessedMessage save(M message, K messageKey, R result) {
        var processedMessage = new ProcessedMessage(messageKey.toString(), message.getClass(), result);

        processedMessageRepository.save(processedMessage);

        return processedMessage;
    }
}
