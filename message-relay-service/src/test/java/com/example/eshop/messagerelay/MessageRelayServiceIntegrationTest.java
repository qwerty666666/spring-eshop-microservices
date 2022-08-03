package com.example.eshop.messagerelay;

import com.example.eshop.kafkatest.RunKafkaTestcontainer;
import com.example.eshop.messagerelay.OutboxProperties.DataSourceProperties;
import com.example.eshop.testutils.IntegrationTest;
import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import com.example.eshop.transactionaloutbox.spring.JdbcTemplateTransactionalOutbox;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
@ActiveProfiles("test")
@IntegrationTest
@RunKafkaTestcontainer
class MessageRelayServiceIntegrationTest {
    public static final String TOPIC = "test_topic";

    @TestConfiguration
    @ImportAutoConfiguration({ EmbeddedDataSourceConfiguration.class })
    public static class TestConfig {
        @Bean
        public Consumer<String, Message> kafkaConsumer(ConsumerFactory<String, Message> consumerFactory) {
            var consumer = consumerFactory.createConsumer("testGroup");
            consumer.subscribe(List.of(TOPIC));
            return consumer;
        }

        @Bean
        public TransactionalOutbox transactionalOutbox(DataSource dataSource) {
            return new JdbcTemplateTransactionalOutbox(dataSource);
        }

        @SneakyThrows
        @Bean
        public OutboxProperties outboxProperties(DataSource dataSource) {
            var simpleDriverDataSource = dataSource.unwrap(SimpleDriverDataSource.class);

            var dataSourcesProps = Map.of("test", new DataSourceProperties()
                    .setUrl(simpleDriverDataSource.getUrl())
                    .setUsername(simpleDriverDataSource.getUsername())
                    .setPassword(simpleDriverDataSource.getPassword())
            );

            return new OutboxProperties()
                    .setDataSources(dataSourcesProps);
        }
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionalOutbox transactionalOutbox;

    @Autowired
    private Consumer<String, Message> consumer;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void setUp() {
        createOutboxTable();
    }

    @SneakyThrows
    private void createOutboxTable() {
        dataSource.getConnection().createStatement().execute("""
                create table transactional_outbox(
                    id            bigint auto_increment,
                    aggregate     varchar(255),
                    aggregate_id  varchar(255),
                    type          varchar(255),
                    topic         varchar(255) not null,
                    payload       bytea,
                    key           bytea,
                    request_id    varchar(255),
                    customer_id   varchar(255),
                    creation_time timestamp not null
                )"""
        );
    }

    @Test
    void whenThereAreNewMessagesInOutbox_thenMessagesArePublishedToBroker() {
        saveNewMessageToOutboxAndAssertMessageIsPublishedToBroker(new Message("testMessage_1"));
        saveNewMessageToOutboxAndAssertMessageIsPublishedToBroker(new Message("testMessage_2"));
    }

    private void saveNewMessageToOutboxAndAssertMessageIsPublishedToBroker(Message message) {
        writeMessageToDatabase(message);
        awaitNextMessageAndAssertEquals(message);
    }

    @SneakyThrows
    private void writeMessageToDatabase(Message message) {
        transactionalOutbox.add(OutboxMessage.builder()
                .topic(TOPIC)
                .payload(objectMapper.writeValueAsBytes(message))
                .type(message.getClass().getName())
                .build()
        );
    }

    @SneakyThrows
    private void awaitNextMessageAndAssertEquals(Message message) {
        // wait for next message
        var record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);

        assertNotEquals(null, record);
        assertEquals(message, record.value());
    }

    private record Message(String message) {
    }
}
