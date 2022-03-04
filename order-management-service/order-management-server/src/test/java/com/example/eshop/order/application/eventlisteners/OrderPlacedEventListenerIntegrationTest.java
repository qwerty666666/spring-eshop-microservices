package com.example.eshop.order.application.eventlisteners;

import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.order.FakeData;
import com.example.eshop.order.application.services.createorder.CreateOrderService;
import com.example.eshop.sharedtest.IntegrationTest;
import com.example.eshop.sharedtest.dbtests.DbTest;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.database.rider.core.api.dataset.DataSet;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@IntegrationTest
@EmbeddedKafka(
        partitions = 1,
        topics = {CheckoutApi.ORDER_PLACED_TOPIC},
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@DbTest
class OrderPlacedEventListenerIntegrationTest {
    @TestConfiguration
    static class Config {
        @Bean
        public KafkaTemplate<String, OrderPlacedEvent> orderPlacedEventKafkaTemplate(
                @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers
        ) {
            return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                    Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers),
                    new StringSerializer(),
                    new JsonSerializer<>(
                            JacksonUtils.enhancedObjectMapper()
                                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    )
            ));
        }
    }

    @Autowired
    private EmbeddedKafkaBroker broker;
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @MockBean
    private OrderPlacedEventMapper orderPlacedEventMapper;
    @MockBean
    private CreateOrderService createOrderService;

    @BeforeEach
    public void setUp() {
        // we should wait because listener container can start after the first message was published
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, broker.getPartitionsPerTopic());
        }
    }

    @Test
    @DataSet(cleanAfter = true)
    void whenProcessDuplicatedEvent_thenEventProcessedOnce() throws ExecutionException, InterruptedException, TimeoutException {
        var event = new OrderPlacedEvent(FakeData.orderDto(), LocalDateTime.now());
        var key = event.order().id().toString();
        var expectedOrder = FakeData.order();

        when(orderPlacedEventMapper.toOrder(event)).thenReturn(expectedOrder);

        for (int i = 0; i < 2; i++) {
            kafkaTemplate.send(CheckoutApi.ORDER_PLACED_TOPIC, key, event)
                    .get(5, TimeUnit.SECONDS);
        }

        verify(createOrderService, timeout(5000).only()).save(expectedOrder);
    }
}
