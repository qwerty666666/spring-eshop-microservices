package com.example.eshop.auth;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be added to a test method to emulate
 * running with a mocked {@link CustomJwtAuthentication} added
 * to {@link SecurityContext}.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithMockCustomJwtAuthenticationSecurityContextFactory.class)
public @interface WithMockCustomJwtAuthentication {
    String customerId();

    String email() default "";
}
