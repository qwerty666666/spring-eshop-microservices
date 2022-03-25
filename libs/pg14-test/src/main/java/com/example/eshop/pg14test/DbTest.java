package com.example.eshop.pg14test;

import com.example.eshop.sharedtest.IntegrationTest;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation extends {@link IntegrationTest} and used by tests
 * which are depends on real Database.
 * <p>
 * It will start and configure database (run all migrations) in
 * Docker container using Testcontainers. The running container
 * is reused across all tests and will be stopped on
 * JVM shutdown.
 * <p>
 * <b>Note</b>: If Docker is not available on the running machine,
 * the tests will fail.
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@IntegrationTest
@ExtendWith(PostgresTestcontainersExtension.class)
@TestPropertySource(properties = { "spring.flyway.enabled=true" })
@DBRider
public @interface DbTest {
}
