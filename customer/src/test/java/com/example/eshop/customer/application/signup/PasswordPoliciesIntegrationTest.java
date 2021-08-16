package com.example.eshop.customer.application.signup;

import com.example.eshop.customer.config.SecurityConfig;
import com.example.eshop.customer.domain.customer.CustomerRepository;
import com.example.eshop.customer.domain.customer.HashedPasswordFactory;
import com.example.eshop.customer.domain.customer.PasswordPolicyException;
import com.example.eshop.customer.domain.customer.UniqueEmailSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SecurityConfig.class)
public class PasswordPoliciesIntegrationTest {
    @Autowired
    HashedPasswordFactory hashedPasswordFactory;

    @MockBean
    CustomerRepository customerRepository;

    @MockBean
    UniqueEmailSpecification uniqueEmailSpecification;

    SignUpService signUpService;

    @BeforeEach
    void setUp() {
        when(uniqueEmailSpecification.isSatisfiedBy(any())).thenReturn(true);
        signUpService = new SignUpServiceImpl(customerRepository, hashedPasswordFactory, uniqueEmailSpecification);
    }

    @Test
    void testPasswordPolicies() {
        assertAll(
                // min length 6
                () -> assertThrowsPasswordPolicyException("qwert", "5 characters password"),

                // digits
                () -> assertThrowsPasswordPolicyException("qwerty", "password without digits"),

                // valid password
                () -> assertDoesNotThrow("qwert1", "Valid password")
        );
    }

    private void assertThrowsPasswordPolicyException(String password, String description) {
        assertThatExceptionOfType(PasswordPolicyException.class)
                .as(description)
                .isThrownBy(() -> signUp(password));
    }

    private void assertDoesNotThrow(String password, String description) {
        assertThatNoException()
                .as(description)
                .isThrownBy(() -> signUp(password));
    }

    private void signUp(String password) {
        signUpService.signUp(new SignUpCommand(
                "firstname",
                "lastname",
                "test@test.test",
                null,
                password
        ));
    }
}
