package com.example.eshop.checkout.config;

import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY, havingValue = "false", matchIfMissing = true)
public class KafkaConfig {
    public static final String DISABLE_KAFKA_CONFIG_PROPERTY = "kafka.disabled";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean("cart-orderPlacedEventKafkaTemplate")
    public KafkaTemplate<String, OrderPlacedEvent> orderPlacedEventKafkaTemplate() {
        return new KafkaTemplate<>(jsonKafkaProducerFactory());
    }

    @Bean("cart-stocksReservationKafkaTemplate")
    public ReplyingKafkaTemplate<String, OrderDto, ReservationResult> stocksReservationKafkaTemplate() {
        return new ReplyingKafkaTemplate<>(
                jsonKafkaProducerFactory(),
                stocksReservationMessageListenerContainer()
        );
    }

    @Bean("cart-stocksReservationMessageListenerContainer")
    public ConcurrentMessageListenerContainer<String, ReservationResult> stocksReservationMessageListenerContainer() {
        return new ConcurrentMessageListenerContainer<>(stocksReservationConsumerFactory(),
                new ContainerProperties(CheckoutApi.RESERVE_STOCKS_REPLY_TOPIC));
    }

    @Bean("cart-stocksReservationConsumerFactory")
    public ConsumerFactory<String, ReservationResult> stocksReservationConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, "checkout",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        JsonDeserializer.TRUSTED_PACKAGES, "*"
                ),
                null,
                () -> {
                    var jsonDeserializer = new JsonDeserializer<>(ReservationResult.class)
                            .trustedPackages("*");
                    return new ErrorHandlingDeserializer<>(jsonDeserializer);
                }
        );
    }

    @Bean("cart-jsonKafkaProducerFactoryConfigs")
    public Map<String, Object> jsonProducerFactoryConfigs() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
        );
    }

    @Bean
    public <T> DefaultKafkaProducerFactory<String, T> jsonKafkaProducerFactory() {
        return new DefaultKafkaProducerFactory<>(
                jsonProducerFactoryConfigs(),
                new StringSerializer(),
                jsonSerializer()
        );
    }

    @Bean
    public <T> JsonSerializer<T> jsonSerializer() {
        var objectMapper = JacksonUtils.enhancedObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new JsonSerializer<>(objectMapper);
    }
}
