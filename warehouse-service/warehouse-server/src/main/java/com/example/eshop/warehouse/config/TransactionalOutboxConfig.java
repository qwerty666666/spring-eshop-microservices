package com.example.eshop.warehouse.config;

import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import com.example.eshop.transactionaloutbox.outboxmessagefactory.DomainEventOutboxMessageFactory;
import com.example.eshop.transactionaloutbox.outboxmessagefactory.JacksonEventSerializer;
import com.example.eshop.transactionaloutbox.outboxmessagefactory.NullRequestIdSupplier;
import com.example.eshop.transactionaloutbox.springdata.JdbcTemplateTransactionalOutbox;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class TransactionalOutboxConfig {
    @Bean
    public TransactionalOutbox transactionalOutbox(DataSource dataSource) {
        return new JdbcTemplateTransactionalOutbox(dataSource);
    }

    @Bean
    public DomainEventOutboxMessageFactory outboxMessageFactory(ObjectMapper objectMapper) {
        return new DomainEventOutboxMessageFactory(
                new JacksonEventSerializer(objectMapper),
                new NullRequestIdSupplier()
        );
    }
}
