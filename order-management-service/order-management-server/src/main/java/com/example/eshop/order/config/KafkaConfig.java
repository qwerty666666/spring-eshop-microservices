package com.example.eshop.order.config;

import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY, havingValue = "false", matchIfMissing = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KafkaConfig {
    public static final String DISABLE_KAFKA_CONFIG_PROPERTY = "kafka.disabled";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Configuration
    @ConditionalOnProperty(value = KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY, havingValue = "false", matchIfMissing = true)
    static class EnableKafkaConfig extends KafkaAutoConfiguration {
        public EnableKafkaConfig(KafkaProperties properties) {
            super(properties);
        }
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent> orderPlacedKafkaListenerContainerFactory(
            ConsumerFactory<String, OrderPlacedEvent> orderPlacedConsumerFactory,
            @Qualifier("orderPlacedErrorHandler") CommonErrorHandler errorHandler
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, OrderPlacedEvent>();

        factory.setConsumerFactory(orderPlacedConsumerFactory);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public CommonErrorHandler orderPlacedErrorHandler(DeadLetterPublishingRecoverer dltRecoverer) {
        // retry 5 times and then publish message to DTL
        return new DefaultErrorHandler(dltRecoverer, new FixedBackOff(3000, 5));
    }

    @Bean
    public ConsumerFactory<String, OrderPlacedEvent> orderPlacedConsumerFactory(
            // Spring can't autowire Map beans, so we use hack wit @Value
            @Value("#{commonConsumerConfigs}") Map<String, Object> commonConsumerConfigs,
            @Qualifier("stringKeyDeserializer") Deserializer<String> keyDeserializer,
            @Qualifier("orderPlacedEventJsonDeserializer") Deserializer<OrderPlacedEvent> valueDeserializer
    ) {
        return new DefaultKafkaConsumerFactory<>(commonConsumerConfigs, keyDeserializer, valueDeserializer);
    }

    @Bean
    @Scope("prototype")
    public Deserializer<String> stringKeyDeserializer() {
        return new ErrorHandlingDeserializer<>(new StringDeserializer()) // NOSONAR close resource
                .keyDeserializer(true);
    }

    @Bean
    @Scope("prototype")
    public Deserializer<OrderPlacedEvent> orderPlacedEventJsonDeserializer() {
        var jsonDeserializer = new JsonDeserializer<>(OrderPlacedEvent.class) // NOSONAR close resource
                .trustedPackages("*");

        return new ErrorHandlingDeserializer<>(jsonDeserializer);
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            ProducerFactory<String, byte[]> byteProducerFactory,
            ProducerFactory<String, Object> jsonProducerFactory
    ) {
        /*
         * TODO preserve original record
         *
         * The problem:
         * Spring try to produce consumed record to DLT. But the original
         * ConsumerRecord has only deserialized key and value and do not have
         * original byte[] value. And therefore we can't produce the same record
         * as we consumed, because to achieve that we must provide exactly the
         * same serializer as the original producer did (and we can't do it
         * of course and potentially we lose data)
         *
         * Possible solution:
         * The only way to preserve original record is to store original key
         * and value in byte[]. We can do it by using custom deserializers
         * (it is the only place where we can access byte[] representation
         * of key and value), which will store key and value in ConsumerRecord
         * headers.
         */

        /*
         * Use different KafkaTemplates for different original record's value.
         *
         * If there are key or value deserialization errors in consumed record
         * (when random data is sent), Spring's ErrorHandlingDeserializer will
         * create empty ConsumerRecord and save original value in byte[] header and
         * DeadLetterPublishingRecoverer use it as value to produce record to DLT.
         * Therefore, we must provide two serializers: for succeeded deserialization
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
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class
        ));
    }

    @Bean
    public ProducerFactory<String, Object> jsonProducerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        ));
    }

    @Bean
    public Map<String, Object> commonConsumerConfigs(AppProperties appProperties) {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, appProperties.getKafka().getConsumerGroup()
        );
    }
}
