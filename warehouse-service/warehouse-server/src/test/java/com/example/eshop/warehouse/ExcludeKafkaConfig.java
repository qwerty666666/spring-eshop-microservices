package com.example.eshop.warehouse;

import com.example.eshop.warehouse.config.KafkaConfig;
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
public @interface ExcludeKafkaConfig {
}
