package com.example.eshop.customer.application.updatecustomer;

import com.example.eshop.customer.application.exceptions.CustomerNotFoundException;
import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.CustomerRepository;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;
import com.example.eshop.customer.domain.customer.UniqueEmailSpecification;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCustomerServiceImpl implements UpdateCustomerService {
    private final CustomerRepository customerRepository;
    private final UniqueEmailSpecification uniqueEmailSpecification;

    public UpdateCustomerServiceImpl(
            CustomerRepository customerRepository,
            UniqueEmailSpecification uniqueEmailSpecification) {
        this.customerRepository = customerRepository;
        this.uniqueEmailSpecification = uniqueEmailSpecification;
    }

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
    }

    private void updateCustomer(Customer customer, UpdateCustomerCommand command) {
        customer.setFirstname(command.firstname());
        customer.setLastname(command.lastname());
        customer.setBirthday(command.birthday());
        customer.setEmail(Email.fromString(command.email()));
    }
}
