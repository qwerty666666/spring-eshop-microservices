package com.example.eshop.pg14test;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * JUnit Extension which starts PostgreSQL Testcontainer.
 * <p>
 * Container is automatically started when Extension is loaded for the first
 * time and reused across all tests.
 * After start, it fills <code>spring.datasource.*</code> properties, therefore
 * after container startup Spring Context can be initialized with
 * container database.
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
class PostgresTestcontainersExtension implements Extension, TestInstancePostProcessor {
    // TODO is there better way to run container before Spring Context initialization ??
    /*
     * The problem is that we must start Postgres Container and fill spring.datasource.*
     * properties before SpringExtension will start bootstrapping. To achieve that we
     * must guarantee this Extension is handled before SpringExtension.
     *
     * In JUnit Extensions are registered in order they are declared on the test class
     * and Extensions from superclass are registered first. So we must place this
     * Extension before SpringExtension, that is very error-prone, especially if we use
     * meta-annotations. So, we try to avoid this. We have 2 options (we stick to the first
     * option):
     *
     * 1. Make this field static to start container before any JUnit extension callback
     *    and therefore datasource params will be initialized before Spring Context will be
     *    bootstrapped.
     *    It seems like JUnit is not guarantee that Extension will be initialized before
     *    it is accessed for the first time. But in current implementation Extensions are
     *    initialized when they are registered for a test class. So, static initialization
     *    is a bit hacky, but it save us from keeping order of Extensions registration.
     *
     * 2. Another way to do this is register container bootstrapping by Spring's
     *    @TestExecutionListeners. @TestExecutionListeners allows us to control order of
     *    listeners execution. But @TestExecutionListeners can be used on class only once,
     *    and it will conflict with @DbRider.
     */
    @SuppressWarnings("rawtypes")
    private static final PostgreSQLContainer POSTGRES_CONTAINER = new PostgreSQLContainer("postgres:14");

    // TODO we should run migrations there, otherwise they will be re-run on every Spring context bootstrap
    // Migrations will be run on every @SpringBootTest which loads Flyway Bean. And therefore:
    // 1. it is context startup overhead
    // 2. if migrations are not idempotent than there will be issues with run migrations multiple times

    static {
        POSTGRES_CONTAINER.start();

        System.setProperty("spring.datasource.url", POSTGRES_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRES_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", POSTGRES_CONTAINER.getPassword());
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        // do nothing, container is initialized in static context
    }
}
