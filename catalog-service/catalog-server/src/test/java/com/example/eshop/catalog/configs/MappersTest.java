package com.example.eshop.catalog.configs;

import com.example.eshop.sharedtest.IntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation which load Spring's Context for Mapstruct's
 * mappers test.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest(classes = MappersConfig.class)
@IntegrationTest
public @interface MappersTest {
}
