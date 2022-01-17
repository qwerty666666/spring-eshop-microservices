package com.example.eshop.transactionaloutbox;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OutboxMessageBuilderTest {
    @Test
    void testMessageShouldContainTopic() {
        assertThrows(IllegalArgumentException.class, () -> OutboxMessage.builder()
                .topic(null)
                .payload("test".getBytes(StandardCharsets.UTF_8))
                .build()
        );
    }
}