package com.example.eshop.rest.requests;

import com.example.eshop.customer.infrastructure.validation.ValidPassword;
import com.example.eshop.sharedkernel.infrastructure.validation.email.ValidEmail;
import org.springframework.lang.Nullable;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

/**
 * Register new customer request DTO
 */
public record SignUpRequest(
        @NotEmpty String firstname,
        @NotEmpty String lastname,
        @ValidEmail String email,
        @Nullable LocalDate birthday,
        @ValidPassword String password) {

}
