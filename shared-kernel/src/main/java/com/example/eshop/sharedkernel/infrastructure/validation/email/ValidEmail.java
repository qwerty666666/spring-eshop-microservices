package com.example.eshop.sharedkernel.infrastructure.validation.email;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The String has to be email address.
 * <p>
 * In contrast to {@link javax.validation.constraints.Email} consider intranet addresses
 * (like "address@server") as invalid
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailConstraintValidator.class)
public @interface ValidEmail {
    String message() default "Invalid Email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
