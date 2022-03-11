package com.example.eshop.catalog.configs;

import com.example.eshop.catalog.config.KafkaConfig;
import com.example.eshop.kafkatest.RunKafka;
import com.example.eshop.sharedtest.IntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation is used by Integration tests which are depends on Kafka.
 * <p>
 * Runs Kafka broker and enable {@link KafkaConfig}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest
@IntegrationTest
@RunKafka
@TestPropertySource(properties = KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY + "=false")
public @interface KafkaTest {
}
