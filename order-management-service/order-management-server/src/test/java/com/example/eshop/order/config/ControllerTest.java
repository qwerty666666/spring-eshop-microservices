package com.example.eshop.order.config;

import com.example.eshop.sharedtest.IntegrationTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation which loads Spring's Context for Controller tests.
 * <p>
 * Should be used with {@link WebMvcTest}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@IntegrationTest
@Import(ControllerTestsConfig.class)
public @interface ControllerTest {
}
