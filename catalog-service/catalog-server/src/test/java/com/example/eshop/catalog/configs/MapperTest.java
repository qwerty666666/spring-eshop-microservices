package com.example.eshop.catalog.configs;

import com.example.eshop.catalog.configs.MapperTest.MapperTestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
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
@SpringBootTest(classes = MapperTestConfig.class)
@ActiveProfiles({ "mapper-test", "test" })
public @interface MapperTest {

    @Configuration
    @Profile("mapper-test")
    @Import(MappersConfig.class)
    class MapperTestConfig {
    }
}
