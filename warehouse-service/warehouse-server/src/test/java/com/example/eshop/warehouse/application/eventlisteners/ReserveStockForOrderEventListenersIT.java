package com.example.eshop.warehouse.application.eventlisteners;

import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.order.CartDto;
import com.example.eshop.checkout.client.order.CartItemDto;
import com.example.eshop.checkout.client.order.DeliveryAddressDto;
import com.example.eshop.checkout.client.order.OrderDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import com.example.eshop.warehouse.application.services.reserve.ReserveStockItemService;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import com.example.eshop.warehouse.domain.StockQuantity;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@EmbeddedKafka(
        partitions = 1,
        topics = { CheckoutApi.RESERVE_STOCKS_TOPIC, CheckoutApi.RESERVE_STOCKS_REPLY_TOPIC },
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class ReserveStockForOrderEventListenersIT {
    @TestConfiguration
    public static class Config {
        @Value("${spring.kafka.bootstrap-servers}")
        private String bootstrapServers;

        @Bean
        public ReplyingKafkaTemplate<String, OrderDto, ReservationResult> reserveStocksKafkaTemplate() {
            return new ReplyingKafkaTemplate<>(
                    new DefaultKafkaProducerFactory<>(Map.of(
                            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
                    )),
                    listenerContainer()
            );
        }

        @Bean
        public ConcurrentMessageListenerContainer<String, ReservationResult> listenerContainer() {
            return new ConcurrentMessageListenerContainer<>(
                    new DefaultKafkaConsumerFactory<>(
                            Map.of(
                                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                                    ConsumerConfig.GROUP_ID_CONFIG, "test-group",
                                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                                    JsonDeserializer.TRUSTED_PACKAGES, "*"
                            )
                    ),
                    new ContainerProperties(CheckoutApi.RESERVE_STOCKS_REPLY_TOPIC)
            );
        }
    }

    private final Ean ean1 = Ean.fromString("1111111111111");
    private final int qty1 = 10;

    private final Ean ean2 = Ean.fromString("2222222222222");
    private final int qty2 = 20;

    private final OrderDto order = new OrderDto(
            UUID.randomUUID(),
            "customerId",
            new CartDto(List.of(
                    new CartItemDto(ean1, Money.USD(10), qty1),
                    new CartItemDto(ean2, Money.USD(10), qty2)
            )),
            Money.USD(300),
            "deliveryServiceId",
            "paymentServiceId",
            new DeliveryAddressDto("fullname", Phone.fromString("+79999999999"), "country", "city", "street", "building", "flat")
    );

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private ReplyingKafkaTemplate<String, OrderDto, ReservationResult> kafkaTemplate;

    @MockBean
    private ReserveStockItemService reserveStockItemService;

    @BeforeEach
    public void setUp() {
        // we should wait because listener container can start after the first message was published
        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, broker.getPartitionsPerTopic());
        }
    }

    @Test
    void givenReserveStockForOrderEvent_whenEventIsPublished_thenReserveStockItemServiceIsCalledAndResponseReturned()
            throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        var expectedReservingQty = Map.of(
                ean1, StockQuantity.of(qty1),
                ean2, StockQuantity.of(qty2)
        );
        var expectedResult = ReservationResult.success();

        when(reserveStockItemService.reserve(expectedReservingQty)).thenReturn(expectedResult);

        // When
        var result = kafkaTemplate.sendAndReceive(new ProducerRecord<>(CheckoutApi.RESERVE_STOCKS_TOPIC, order),
                        Duration.ofSeconds(5))
                .get(5, TimeUnit.SECONDS)
                .value();

        // Then
        // check that event is handled in given timeout
        assertThat(result).isEqualTo(expectedResult);
        verify(reserveStockItemService).reserve(expectedReservingQty);
    }
}