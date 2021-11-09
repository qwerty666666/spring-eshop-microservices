package com.example.eshop.customer.application.updatecustomer;

import com.example.eshop.customer.application.exceptions.CustomerNotFoundException;
import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.CustomerRepository;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;
import com.example.eshop.customer.domain.customer.UniqueEmailSpecification;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateCustomerServiceImpl implements UpdateCustomerService {
    private final CustomerRepository customerRepository;
    private final UniqueEmailSpecification uniqueEmailSpecification;

    @Override
    @Transactional
    public void updateCustomer(UpdateCustomerCommand command) {
        var customer = customerRepository.findById(command.id())
                .orElseThrow(() -> new CustomerNotFoundException("Customer " + command.id() + " not found"));

        var newEmail = Email.fromString(command.email());
        if (!newEmail.equals(customer.getEmail()) && !uniqueEmailSpecification.isSatisfiedBy(newEmail)) {
            throw new EmailAlreadyExistException("Customer with email " + newEmail + " already exists");
        }

        updateCustomer(customer, command);

        log.info("Customer updated (id: {})", customer.getId());
    }

    private void updateCustomer(Customer customer, UpdateCustomerCommand command) {
        customer.setFirstname(command.firstname());
        customer.setLastname(command.lastname());
        customer.setBirthday(command.birthday());
        customer.setEmail(Email.fromString(command.email()));
    }
}
