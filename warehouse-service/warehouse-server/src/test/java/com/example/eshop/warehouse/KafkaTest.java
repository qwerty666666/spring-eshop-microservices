package com.example.eshop.warehouse;

import com.example.eshop.kafkatest.RunKafka;
import com.example.eshop.sharedtest.IntegrationTest;
import com.example.eshop.warehouse.config.KafkaConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
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
@TestPropertySource(properties = { KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY + "=false" })
public @interface KafkaTest {
}
