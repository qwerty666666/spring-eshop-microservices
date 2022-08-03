package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Serialize {@link DomainEvent} to JSON byte[]
 */
@RequiredArgsConstructor
public class JacksonEventSerializer implements EventSerializer {
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public byte[] apply(DomainEvent domainEvent) {
        return objectMapper.writeValueAsBytes(domainEvent);
    }
}
