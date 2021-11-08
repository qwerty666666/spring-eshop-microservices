package com.example.eshop.customer.application.signup;

import com.example.eshop.customer.domain.customer.CustomerCreatedEvent;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import com.example.eshop.sharedtest.dbtests.DbTest;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DbTest
@SpringBootTest
class SignUpServiceIntegrationTest {
    @TestConfiguration
    public static class Config {
        // Spring can't mock ApplicationEventPublisher. So use this
        // weird workaround to do it.
        // TODO may be it is better to wrap ApplicationEventPublished in our own interface? (but we'll lose IDEA navigation support for events)
        @Bean
        @Primary
        ApplicationEventPublisher eventPublisher() {
            return mock(ApplicationEventPublisher.class);
        }
    }

    @Autowired
    SignUpService signUpService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Test
    @DataSet(value = "customers.yml")
    @ExpectedDataSet(value = "expectedSignUpCustomers.yml", ignoreCols = { "id", "password" }, orderBy = "email")
    @DBUnit(cacheConnection = false, caseSensitiveTableNames = true)
    void whenSignUpCustomer_thenDbUpdatedAndCustomerCreatedEventPublished() {
        // Given
        var firstname = "firstname";
        var lastname = "lastname";
        var email = "test@test.test";
        var password = "pass123";
        var birthday = LocalDate.of(1990, 10, 25);

        // When
        var customer = signUpService.signUp(new SignUpCommand(
                firstname,
                lastname,
                email,
                birthday,
                password
        ));

        // Then
        assertAll(
                () -> assertThat(customer.getFirstname()).as("firstname").isEqualTo(firstname),
                () -> assertThat(customer.getLastname()).as("lastname").isEqualTo(lastname),
                () -> assertThat(customer.getBirthday()).as("birthday").isEqualTo(birthday),
                () -> assertThat(customer.getEmail()).as("email").isEqualTo(Email.fromString(email))
        );

        verify(eventPublisher).publishEvent(new CustomerCreatedEvent(customer.getId().toString()));
    }
}
