package com.example.eshop.rest.requests;

import com.example.eshop.sharedkernel.infrastructure.validation.email.ValidEmail;
import org.springframework.lang.Nullable;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

public record UpdateCustomerRequest(
    @NotEmpty String firstname,
    @NotEmpty String lastname,
    @ValidEmail String email,
    @Nullable LocalDate birthday) {

}
