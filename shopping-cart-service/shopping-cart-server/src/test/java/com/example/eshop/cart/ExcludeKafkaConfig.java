package com.example.eshop.cart;

import com.example.eshop.cart.config.KafkaConfig;
import com.example.eshop.cart.config.TestKafkaConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestPropertySource(properties = { KafkaConfig.DISABLE_KAFKA_CONFIG_PROPERTY + "=true" })
@ImportAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@Import(TestKafkaConfig.class)
public @interface ExcludeKafkaConfig {
}
