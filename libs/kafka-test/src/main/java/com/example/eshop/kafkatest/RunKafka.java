package com.example.eshop.kafkatest;

import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.test.context.EmbeddedKafka;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Run embedded Kafka.
 * <p>
 * The running broker is reused across test cases and
 * {@code auto.create.topics.enable} is true by default,
 * therefore there is no need to create topics.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EmbeddedKafka
public @interface RunKafka {
    @AliasFor(annotation = EmbeddedKafka.class)
    int partitions() default 1;

    @AliasFor(annotation = EmbeddedKafka.class)
    String[] topics() default { };

    @AliasFor(annotation = EmbeddedKafka.class)
    String bootstrapServersProperty() default "spring.kafka.bootstrap-servers";
}
