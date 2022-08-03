package com.example.eshop.order.infrastructure.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, Long> {
    boolean existsByMessageKeyAndMessageClass(String messageKey, Class<?> messageClass);
}
