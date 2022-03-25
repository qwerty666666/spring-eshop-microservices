package com.example.eshop.kafkatest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * JUnit Extension which starts Kafka Testcontainer.
 * <p>
 * Container is automatically started when Extension is loaded for the first
 * time and reused across all tests.
 * After start, it fills <code>spring.kafka.bootstrap-servers</code> properties.
 * <p>
 * This container can't be stopped manually. It will be stopped automatically
 * on JVM shutdown.
 * <p>
 * This class designed as singleton and is not thread-safe. Anyway we can't run
 * tests in parallel with testcontainers (From docs: <code>Note: This extension
 * has only been tested with sequential test execution. Using it with parallel
 * test execution is unsupported and may have unintended side effects.</code>)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class KafkaTestcontainersExtension implements Extension, TestInstancePostProcessor {
    // TODO is there better way to run container before Spring Context initialization ??
    // There is the same problem as with PostgresTestcontainersExtension.
    // See PostgresTestcontainersExtension for details.
    private static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.1"));

    static {
        KAFKA.start();

        System.setProperty("spring.kafka.bootstrap-servers", KAFKA.getBootstrapServers());
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        // do nothing, container is initialized in static context
    }
}
