package com.example.eshop.customer.infrastructure.specifications;

import com.example.eshop.customer.domain.customer.CustomerRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UniqueEmailSpecificationImplTest {
    @Test
    void whenCustomerWithGivenEmailAlreadyExists_thenReturnFalse() {
        var email = Email.fromString("rick-sanchez@example.com");
        var customerRepository = mock(CustomerRepository.class);
        when(customerRepository.existsByEmail(email)).thenReturn(true);

        var uniqueEmailSpecification = new UniqueEmailSpecificationImpl(customerRepository);

        assertThat(uniqueEmailSpecification.isSatisfiedBy(email)).isFalse();
    }

    @Test
    void whenThereIsNoCustomerWithGivenEmail_thenReturnTrue() {
        var email = Email.fromString("rick-sanchez@example.com");
        var customerRepository = mock(CustomerRepository.class);
        when(customerRepository.existsByEmail(email)).thenReturn(false);

        var uniqueEmailSpecification = new UniqueEmailSpecificationImpl(customerRepository);

        assertThat(uniqueEmailSpecification.isSatisfiedBy(email)).isTrue();
    }
}
