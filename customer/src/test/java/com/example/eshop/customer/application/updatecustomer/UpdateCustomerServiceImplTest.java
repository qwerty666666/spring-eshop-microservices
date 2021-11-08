package com.example.eshop.customer.application.updatecustomer;

import com.example.eshop.customer.application.exceptions.CustomerNotFoundException;
import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.Customer.CustomerId;
import com.example.eshop.customer.domain.customer.CustomerRepository;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;
import com.example.eshop.customer.domain.customer.UniqueEmailSpecification;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateCustomerServiceImplTest {
    @Test
    void givenNewEmailWhichAlreadyInUseByAnotherCustomer_whenUpdateCustomer_thenThrowEmailAlreadyExistException() {
        // Given
        var email = "test@test.test";
        var id = new CustomerId("1");

        var uniqueEmailSpecification = mock(UniqueEmailSpecification.class);
        when(uniqueEmailSpecification.isSatisfiedBy(Email.fromString(email))).thenReturn(false);

        var customerRepository = mock(CustomerRepository.class);
        when(customerRepository.findById(id)).thenReturn(Optional.of(mock(Customer.class)));

        var service = new UpdateCustomerServiceImpl(customerRepository, uniqueEmailSpecification);
        var command = new UpdateCustomerCommand(
                id,
                "firstname",
                "lastname",
                email,
                null
        );

        // When + Then
        assertThatExceptionOfType(EmailAlreadyExistException.class)
                .isThrownBy(() -> service.updateCustomer(command));

        verify(uniqueEmailSpecification).isSatisfiedBy(Email.fromString(email));
    }

    @Test
    void givenNotExistedCustomerId_whenUpdateCustomer_thenThrowCustomerNotFoundException() {
        // Given
        var id = new CustomerId("1");

        var customerRepository = mock(CustomerRepository.class);
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        var uniqueEmailSpecification = mock(UniqueEmailSpecification.class);

        var service = new UpdateCustomerServiceImpl(customerRepository, uniqueEmailSpecification);
        var command = new UpdateCustomerCommand(
                id,
                "firstname",
                "lastname",
                "test@test.test",
                null
        );

        // When + Then
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.updateCustomer(command));

        verify(customerRepository).findById(id);
    }
}