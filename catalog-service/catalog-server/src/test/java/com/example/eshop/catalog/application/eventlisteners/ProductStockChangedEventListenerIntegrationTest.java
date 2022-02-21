package com.example.eshop.catalog.application.eventlisteners;

import com.example.eshop.catalog.config.AppProperties;
import com.example.eshop.catalog.config.KafkaConfig;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.ProductRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedtest.IntegrationTest;
import com.example.eshop.warehouse.client.WarehouseApi;
import com.example.eshop.warehouse.client.events.ProductStockChangedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@IntegrationTest
@EmbeddedKafka(
        partitions = 1,
        topics = WarehouseApi.STOCK_CHANGED_TOPIC,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class ProductStockChangedEventListenerIntegrationTest {
    @Configuration
    @EnableConfigurationProperties(AppProperties.class)
    @Import({ KafkaConfig.class, ProductStockChangedEventListener.class })
    public static class Config {
        @Value("${spring.kafka.bootstrap-servers}")
        private String bootstrapServers;

        @Bean
        public KafkaTemplate<String, ProductStockChangedEvent> productStockChangedEventKafkaTemplate() {
            return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
            )));
        }
    }

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private KafkaTemplate<String, ProductStockChangedEvent> kafkaTemplate;

    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        // we should wait because listener container can start after the first message was published
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, broker.getPartitionsPerTopic());
        }
    }

    @Test
    void givenProductStockChangedEvent_whenEventIsPublished_thenSkuQuantityIsUpdated() throws ExecutionException, InterruptedException {
        // Given
        var ean = Ean.fromString("1231231231231");
        var qty = 5;

        var productMock = mock(Product.class);
        when(productRepository.findByEan(ean)).thenReturn(Optional.of(productMock));

        // When
        kafkaTemplate.send(WarehouseApi.STOCK_CHANGED_TOPIC, new ProductStockChangedEvent(ean, qty)).get();

        // Then
        // check that event is handled in given timeout
        verify(productMock, timeout(30000)).setSkuAvailableQuantity(ean, qty);
    }
}
