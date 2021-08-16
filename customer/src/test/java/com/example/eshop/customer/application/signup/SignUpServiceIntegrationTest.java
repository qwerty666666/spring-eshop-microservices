package com.example.eshop.customer.application.signup;

import com.example.eshop.customer.domain.customer.CustomerRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@DBRider
class SignUpServiceIntegrationTest {
    @Autowired
    SignUpService signUpService;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @DataSet(value = "customers.yml")
    @ExpectedDataSet(value = "expectedSignUpCustomers.yml", ignoreCols = { "id", "password" })
    void whenSignUpCustomer_thenDbUpdated() {
        var firstname = "firstname";
        var lastname = "lastname";
        var email = "test@test.test";
        var password = "pass123";
        var birthday = LocalDate.of(1990, 10, 25);

        var customer = signUpService.signUp(new SignUpCommand(
                firstname,
                lastname,
                email,
                birthday,
                password
        ));

        assertAll(
                () -> assertThat(customer.getFirstname()).isEqualTo(firstname).as("firstname"),
                () -> assertThat(customer.getLastname()).isEqualTo(lastname).as("lastname"),
                () -> assertThat(customer.getBirthday()).isEqualTo(birthday).as("birthday"),
                () -> assertThat(customer.getEmail()).isEqualTo(Email.fromString(email)).as("email")
        );
    }
}
