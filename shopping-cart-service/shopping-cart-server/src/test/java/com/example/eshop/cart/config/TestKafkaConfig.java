package com.example.eshop.cart.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY, havingValue = "true")
public class TestKafkaConfig {
}
