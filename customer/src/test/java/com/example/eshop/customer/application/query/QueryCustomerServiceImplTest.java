package com.example.eshop.customer.application.query;

import com.example.eshop.customer.application.exceptions.CustomerNotFoundException;
import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.CustomerRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QueryCustomerServiceImplTest {
    @Test
    void givenNotExistedEmail_whenQueryCustomerByEmail_thenThrowCustomerNotFoundException() {
        // Given
        var email = "test@test.test";

        var customerRepository = mock(CustomerRepository.class);
        when(customerRepository.findByEmail(eq(Email.fromString(email)))).thenReturn(Optional.empty());

        var service = new QueryCustomerServiceImpl(customerRepository);

        // When + Then
        assertThatExceptionOfType(CustomerNotFoundException.class)
                .isThrownBy(() -> service.getByEmail(email));

        verify(customerRepository).findByEmail(eq(Email.fromString(email)));
    }

    @Test
    void givenEmail_whenQueryCustomerByEmail_thenReturnCustomer() {
        // Given
        var email = "test@test.test";
        var expected = mock(Customer.class);

        var customerRepository = mock(CustomerRepository.class);
        when(customerRepository.findByEmail(eq(Email.fromString(email)))).thenReturn(Optional.of(expected));

        var service = new QueryCustomerServiceImpl(customerRepository);

        // When
        var actual = service.getByEmail(email);

        // Then
        assertThat(actual).isSameAs(expected);

        verify(customerRepository).findByEmail(eq(Email.fromString(email)));
    }
}