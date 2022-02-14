package com.example.eshop.warehouse.config;

import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "kafka.disabled", havingValue = "false", matchIfMissing = true)
@Import(KafkaAutoConfiguration.class)
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Configuration
    @EnableKafka
    static class EnableKafkaConfig {}

    @Bean
    public ConsumerFactory<String, OrderDto> reserveStocksForOrderConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, "warehouse",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        JsonDeserializer.TRUSTED_PACKAGES, "*"
                ),
                null,
                () -> {
                    var jsonDeserializer = new JsonDeserializer<>(OrderDto.class)
                            .trustedPackages("*");
                    return new ErrorHandlingDeserializer<>(jsonDeserializer);
                }
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderDto> reserveStocksForOrderKafkaListenerContainerFactory(
            DeadLetterPublishingRecoverer dltRecoverer,
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, OrderDto>();

        factory.setConsumerFactory(reserveStocksForOrderConsumerFactory());
        factory.setErrorHandler(new SeekToCurrentErrorHandler(dltRecoverer, new FixedBackOff(3000, 10)));
        factory.setReplyTemplate(kafkaTemplate);

        return factory;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            ProducerFactory<String, byte[]> byteProducerFactory,
            ProducerFactory<Object, Object> jsonProducerFactory
    ) {
        Map<Class<?>, KafkaOperations<?, ?>> templates = new LinkedHashMap<>();
        templates.put(byte[].class, new KafkaTemplate<>(byteProducerFactory));
        templates.put(Object.class, new KafkaTemplate<>(jsonProducerFactory));

        var dltRecoverer = new DeadLetterPublishingRecoverer(templates);
        dltRecoverer.setRetainExceptionHeader(true);

        return dltRecoverer;
    }

    @Bean
    public ProducerFactory<String, byte[]> bytesProducerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class
        ));
    }

    @Bean
    public ProducerFactory<Object, Object> jsonProducerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        ));
    }
}
