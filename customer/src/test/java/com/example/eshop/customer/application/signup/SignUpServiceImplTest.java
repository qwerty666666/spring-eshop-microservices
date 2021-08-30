package com.example.eshop.customer.application.signup;

import com.example.eshop.customer.domain.customer.*;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class SignUpServiceImplTest {
    @Test
    void givenCustomer_whenSignUpCustomerWithTheSameEmail_thenThrowEmailAlreadyExistException() {
        // Given
        var email = "test@test.test";

        var uniqueEmailSpecification = mock(UniqueEmailSpecification.class);
        when(uniqueEmailSpecification.isSatisfiedBy(eq(Email.fromString(email)))).thenReturn(false);

        var customerRepository = mock(CustomerRepository.class);
        var passwordFactory = mock(HashedPasswordFactory.class);
        var eventPublisher = mock(ApplicationEventPublisher.class);

        var service = new SignUpServiceImpl(customerRepository, passwordFactory, uniqueEmailSpecification, eventPublisher);

        var signUpCommand = new SignUpCommand(
                "firstname",
                "lastname",
                email,
                null,
                "pass"
        );

        // When + Then
        assertThatExceptionOfType(EmailAlreadyExistException.class)
                .isThrownBy(() -> service.signUp(signUpCommand));
        verify(uniqueEmailSpecification).isSatisfiedBy(eq(Email.fromString(email)));
    }
}
