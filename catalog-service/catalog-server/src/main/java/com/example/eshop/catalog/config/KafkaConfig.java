package com.example.eshop.catalog.config;

import com.example.eshop.warehouse.client.events.ProductStockChangedEvent;
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
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KafkaConfig {
    public static final String DISABLE_KAFKA_CONFIG_PROPERTY = "kafka.disabled";

    @Configuration
    @ConditionalOnProperty(value = KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY, havingValue = "true")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    static class DisabledKafkaConfig {
        @Configuration
        @ComponentScan(excludeFilters = {
                @Filter(type = FilterType.REGEX, pattern = "com.example.eshop.catalog.eventlisteners.*")
        })
        static class ExcludeKafkaConfig {
        }
    }

    @Configuration
    @ConditionalOnProperty(value = KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY, havingValue = "false", matchIfMissing = true)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    static class EnabledKafkaConfig {
        @Value("${spring.kafka.bootstrap-servers}")
        private String bootstrapServers;

        @Configuration
        @ImportAutoConfiguration(KafkaAutoConfiguration.class)
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        static class EnableKafkaConfig {
        }

        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, ProductStockChangedEvent> productStockChangedKafkaListenerContainerFactory(
                ConsumerFactory<String, ProductStockChangedEvent> consumerFactory,
                @Qualifier("productStockChangedErrorHandler") CommonErrorHandler errorHandler
        ) {
            var factory = new ConcurrentKafkaListenerContainerFactory<String, ProductStockChangedEvent>();

            factory.setConsumerFactory(consumerFactory);
            factory.setCommonErrorHandler(errorHandler);

            return factory;
        }

        @Bean
        public ConsumerFactory<String, ProductStockChangedEvent> productStockChangedConsumerFactory(
                @Value("#{commonConsumerConfigs}") Map<String, Object> commonConsumerConfigs,
                Deserializer<String> keyDeserializer,
                Deserializer<ProductStockChangedEvent> valueDeserializer
        ) {
            return new DefaultKafkaConsumerFactory<>(commonConsumerConfigs, keyDeserializer, valueDeserializer);
        }

        @Bean
        public CommonErrorHandler productStockChangedErrorHandler(DeadLetterPublishingRecoverer dltRecoverer) {
            // retry 5 times and then publish message to DTL
            return new DefaultErrorHandler(dltRecoverer, new FixedBackOff(3000, 5));
        }

        @Bean
        @Scope("prototype")
        public Deserializer<String> stringKeyDeserializer() {
            return new ErrorHandlingDeserializer<>(new StringDeserializer()) // NOSONAR close resource
                    .keyDeserializer(true);
        }

        @Bean
        @Scope("prototype")
        public Deserializer<ProductStockChangedEvent> productStockChangedEventJsonDeserializer() {
            var jsonDeserializer = new JsonDeserializer<>(ProductStockChangedEvent.class) // NOSONAR close resource
                    .trustedPackages("*");

            return new ErrorHandlingDeserializer<>(jsonDeserializer);
        }

        @Bean
        public Map<String, Object> commonConsumerConfigs(AppProperties appProperties) {
            return Map.of(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                    ConsumerConfig.GROUP_ID_CONFIG, appProperties.getKafka().getConsumerGroup()
            );
        }

        @Bean
        public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
                ProducerFactory<String, byte[]> byteProducerFactory,
                ProducerFactory<String, Object> jsonProducerFactory
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
    }
}
