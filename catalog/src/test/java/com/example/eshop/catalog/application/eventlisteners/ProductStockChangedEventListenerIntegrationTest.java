package com.example.eshop.catalog.application.eventlisteners;

import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.ProductRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.warehouse.client.WarehouseApi;
import com.example.eshop.warehouse.client.events.ProductStockChangedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@EmbeddedKafka(
        partitions = 1,
        topics = WarehouseApi.STOCK_CHANGED_TOPIC,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@ActiveProfiles("catalog-dev")
class ProductStockChangedEventListenerIntegrationTest {
    @TestConfiguration
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
    private KafkaTemplate<String, ProductStockChangedEvent> kafkaTemplate;

    @MockBean
    private ProductRepository productRepository;

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
