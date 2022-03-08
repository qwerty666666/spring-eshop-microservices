package com.example.eshop.cart.testconfig;

import com.example.eshop.cart.config.KafkaConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@ConditionalOnProperty(value = KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY, havingValue = "true")
public class KafkaTestsConfig {
}
