package com.example.eshop.messagerelay;

import com.example.eshop.transactionaloutbox.OutboxMessage;
import com.example.eshop.transactionaloutbox.TransactionalOutbox;
import com.example.eshop.transactionaloutbox.messagerelay.MessageRelay;
import com.example.eshop.transactionaloutbox.springdata.JdbcTemplateTransactionalOutbox;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(properties = {
        "logging.level.org.apache.kafka=info"  // hide listener log noise
})
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@EmbeddedKafka(
        partitions = 1,
        topics = DefaultMessageRelayIntegrationTest.TOPIC,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class DefaultMessageRelayIntegrationTest {
    public static final String TOPIC = "test_topic";

    @Configuration
    @Import({ KafkaAutoConfiguration.class, JacksonAutoConfiguration.class })
    public static class C {
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

        @Bean
        public MessageRelay messageRelay(DataSource dataSource, KafkaTemplate<String, byte[]> kafkaTemplate) {
            return new DefaultMessageRelay("test", dataSource, kafkaTemplate, 1, 1, TimeUnit.SECONDS);
        }
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionalOutbox transactionalOutbox;

    @Autowired
    private MessageRelay messageRelay;

    @Autowired
    private Consumer<String, Message> consumer;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void setUp() {
        createOutboxTable();
        startMessageRelay();
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
                    creation_time timestamp not null
                )"""
        );
    }

    private void startMessageRelay() {
        messageRelay.start();
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
