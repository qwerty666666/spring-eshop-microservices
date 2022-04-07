package com.example.eshop.messagerelay.config;

import com.example.eshop.messagerelay.brokerproducers.KafkaBrokerProducer;
import com.example.eshop.messagerelay.brokerproducers.TracingBrokerProducerDecorator;
import com.example.eshop.transactionaloutbox.messagerelay.BrokerProducer;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.propagation.Propagator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class MessageRelayConfig {
    @Bean
    public BrokerProducer kafkaBrokerProducer(KafkaTemplate<String, byte[]> kafkaTemplate,
            Tracer tracer, Propagator propagator) {
        return new TracingBrokerProducerDecorator(new KafkaBrokerProducer(kafkaTemplate), tracer, propagator);
    }
}
