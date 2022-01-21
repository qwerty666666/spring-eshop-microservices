package com.example.eshop.catalog.config;

import com.example.eshop.warehouse.client.events.ProductStockChangedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration("catalog-kafkaConfig")
@ConditionalOnProperty(value = KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY, havingValue = "false", matchIfMissing = true)
public class KafkaConfig {
    public static final String DISABLE_KAFKA_CONFIG_PROPERTY = "kafka.disable";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, ProductStockChangedEvent> productStockChangedConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, "catalog",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        JsonDeserializer.TRUSTED_PACKAGES, "*"
                ),
                null,
                () -> {
                    var jsonDeserializer = new JsonDeserializer<>(ProductStockChangedEvent.class)
                            .trustedPackages("*");
                    return new ErrorHandlingDeserializer<>(jsonDeserializer);
                }
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductStockChangedEvent> productStockChangedKafkaListenerContainerFactory(
            DeadLetterPublishingRecoverer dltRecoverer
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ProductStockChangedEvent>();

        factory.setConsumerFactory(productStockChangedConsumerFactory());
        factory.setErrorHandler(new SeekToCurrentErrorHandler(dltRecoverer, new FixedBackOff(3000, 10)));

        return factory;
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            ProducerFactory<String, byte[]> byteProducerFactory,
            ProducerFactory<Object, Object> jsonProducerFactory
    ) {
        /*
         * Use different KafkaTemplates for different original record's value.
         *
         * Spring try to save original record's value to DLT. To produce record
         * to DLT, we need to provide serializer as original producer does. But
         * when there is a deserialization error in consumed record (when random
         * data is sent), the original serializer will fail too. In this case
         * Spring's ErrorHandlingDeserializer saves consumed record value in
         * byte[] header and Spring use it as value to produce record to DLT.
         * Therefore, we should provide two serializers: for succeeded deserialization
         * and for byte[] value.
         *
         * Spring will use the last serializer when no other is satisfied, so we use
         * Object.class in last item in templates.
         *
         * see: https://docs.spring.io/spring-kafka/docs/current/reference/html/#dead-letters
         */
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
