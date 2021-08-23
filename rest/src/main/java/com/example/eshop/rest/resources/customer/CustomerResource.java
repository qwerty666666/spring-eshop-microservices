package com.example.eshop.rest.resources.customer;

import com.example.eshop.customer.domain.customer.Customer;
import org.springframework.lang.Nullable;
import java.time.LocalDate;

public class CustomerResource {
    public String id;
    public String firstname;
    public String lastname;
    public String email;
    @Nullable
    public LocalDate birthday;

    public CustomerResource(Customer customer) {
        this.id = customer.getId().toString();
        this.firstname = customer.getFirstname();
        this.lastname = customer.getLastname();
        this.email = customer.getEmail().toString();
        this.birthday = customer.getBirthday();
    }
}
