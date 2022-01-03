package com.example.eshop.warehouse.config;

import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import com.example.eshop.transactionaloutbox.outboxmessagefactory.DefaultOutboxMessageFactory;
import com.example.eshop.transactionaloutbox.outboxmessagefactory.JacksonEventSerializer;
import com.example.eshop.transactionaloutbox.outboxmessagefactory.NullRequestIdSupplier;
import com.example.eshop.transactionaloutbox.outboxmessagefactory.OutboxMessageFactory;
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
    public OutboxMessageFactory outboxMessageFactory(ObjectMapper objectMapper) {
        return new DefaultOutboxMessageFactory(
                new JacksonEventSerializer(objectMapper),
                new NullRequestIdSupplier()
        );
    }
}
