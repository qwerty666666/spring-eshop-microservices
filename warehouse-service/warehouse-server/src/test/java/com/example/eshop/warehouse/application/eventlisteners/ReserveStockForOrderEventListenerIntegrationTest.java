package com.example.eshop.warehouse.application.eventlisteners;

import com.example.eshop.catalog.client.model.SkuWithProductDto;
import com.example.eshop.catalog.client.model.ProductDto;
import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.events.orderplacedevent.CartDto;
import com.example.eshop.checkout.client.events.orderplacedevent.CartItemDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryAddressDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryServiceDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentDto;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentServiceDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import com.example.eshop.sharedtest.dbtests.DbTest;
import com.example.eshop.warehouse.KafkaTest;
import com.example.eshop.warehouse.application.services.reserve.ReserveStockItemService;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import com.example.eshop.warehouse.domain.StockQuantity;
import com.github.database.rider.core.api.dataset.DataSet;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
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
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.ActiveProfiles;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@KafkaTest
@DbTest
class ReserveStockForOrderEventListenerIntegrationTest {
    @TestConfiguration
    static class Config {
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
    private final Money price1 = Money.USD(10);
    private final int qty1 = 10;

    private final Ean ean2 = Ean.fromString("2222222222222");
    private final Money price2 = Money.USD(10);
    private final int qty2 = 20;

    private final OrderDto order = OrderDto.builder()
            .id(UUID.randomUUID())
            .customerId("customerId")
            .cart(new CartDto(
                    price1.add(price2),
                    List.of(
                            new CartItemDto(
                                    ean1,
                                    price1,
                                    qty1,
                                    SkuWithProductDto.builder()
                                            .ean(ean1)
                                            .price(price1)
                                            .product(ProductDto.builder().build())
                                            .build()
                            ),
                            new CartItemDto(
                                    ean2,
                                    price2,
                                    qty2,
                                    SkuWithProductDto.builder()
                                            .ean(ean2)
                                            .price(price2)
                                            .product(ProductDto.builder().build())
                                            .build()
                            )
                    )
            ))
            .totalPrice(Money.USD(300))
            .delivery(new DeliveryDto(
                    new DeliveryAddressDto("fullname", Phone.fromString("+79999999999"), "country", "city", "street", "building", "flat"),
                    new DeliveryServiceDto("deliveryServiceId", "deliveryServiceName"),
                    Money.USD(3)
            ))
            .payment(new PaymentDto(
                    new PaymentServiceDto("paymentServiceId", "paymentServiceName")
            ))
            .build();

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
            ContainerTestUtils.waitForAssignment(messageListenerContainer, 1);
        }
    }

    @Test
    @DataSet(cleanAfter = true)
    void givenReserveStockForOrderEvent_whenEventIsPublished_thenReserveStockItemServiceIsCalledAndResponseReturned()
            throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        var reservingQty = Map.of(
                ean1, StockQuantity.of(qty1),
                ean2, StockQuantity.of(qty2)
        );
        var expectedResult = ReservationResult.success();
        when(reserveStockItemService.reserve(reservingQty)).thenReturn(expectedResult);

        // When
        ReservationResult reservationResult = null;

        for (int i = 0; i < 2; i++) {
            var producerRecord = new ProducerRecord<>(CheckoutApi.RESERVE_STOCKS_TOPIC, order.id().toString(), order);

            reservationResult = kafkaTemplate.sendAndReceive(producerRecord, Duration.ofSeconds(5))
                    .get(5, TimeUnit.SECONDS)
                    .value();
        }

        // Then
        assertThat(reservationResult).isEqualTo(expectedResult);
        verify(reserveStockItemService, timeout(5000).only()).reserve(reservingQty);
    }
}
