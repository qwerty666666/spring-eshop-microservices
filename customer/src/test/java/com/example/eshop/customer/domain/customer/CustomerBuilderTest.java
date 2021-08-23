package com.example.eshop.customer.domain.customer;

import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;

class CustomerBuilderTest {
    private static final String FIRSTNAME = "Rick";
    private static final String LASTNAME = "Sanchez";
    private static final Email EMAIL = Email.fromString("rick-sanchez@exmaple.com");
    private static final HashedPassword PASSWORD = HashedPassword.fromHash("pass");

    @Test
    void testValidation() {
        //noinspection ConstantConditions
        assertAll(
                // firstname
                () -> assertFirstName(null, "Null firstname"),
                () -> assertFirstName("", "Empty firstname"),

                // lastname
                () -> assertLastName(null, "Null lastname"),
                () -> assertLastName("", "Empty lastname"),

                // email
                () -> assertEmail(null, "Null email"),

                // password
                () -> assertPassword(null, "Null password"),

                // Valid Customer
                () -> assertThatNoException()
                        .as("Valid customer")
                        .isThrownBy(() -> callBuilder(FIRSTNAME, LASTNAME, EMAIL, PASSWORD))
        );
    }

    void assertFirstName(String firstname, String description) {
        assertBuilder(firstname, LASTNAME, EMAIL, PASSWORD, description);
    }

    void assertLastName(String lastname, String description) {
        assertBuilder(FIRSTNAME, lastname, EMAIL, PASSWORD, description);
    }

    void assertEmail(Email email, String description) {
        assertBuilder(FIRSTNAME, LASTNAME, email, PASSWORD, description);
    }

    void assertPassword(HashedPassword password, String description) {
        assertBuilder(FIRSTNAME, LASTNAME, EMAIL, password, description);
    }

    void assertBuilder(String firstname, String lastname, Email email, HashedPassword password, String description) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> callBuilder(firstname, lastname, email, password))
                .as(description);
    }

    void callBuilder(String firstname, String lastname, Email email, HashedPassword password) {
        Customer.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .password(password)
                .build();
    }
}
