package com.example.eshop.customer.application.updatecustomer;

import com.example.eshop.customer.domain.customer.Customer.CustomerId;
import com.example.eshop.sharedkernel.infrastructure.validation.email.ValidEmail;
import org.springframework.lang.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateCustomerCommand(
        @NotNull CustomerId id,
        @NotEmpty String firstname,
        @NotEmpty String lastname,
        @ValidEmail String email,
        @Nullable LocalDate birthday
) {
}
