package com.example.eshop.customer.application.signup;

import com.example.eshop.customer.domain.customer.*;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignUpServiceImpl implements SignUpService {
    private final CustomerRepository customerRepository;
    private final HashedPasswordFactory hashedPasswordFactory;
    private final UniqueEmailSpecification uniqueEmailSpecification;

    public SignUpServiceImpl(
            CustomerRepository customerRepository,
            HashedPasswordFactory hashedPasswordFactory,
            UniqueEmailSpecification uniqueEmailSpecification
    ) {
        this.customerRepository = customerRepository;
        this.hashedPasswordFactory = hashedPasswordFactory;
        this.uniqueEmailSpecification = uniqueEmailSpecification;
    }

    @Override
    @Transactional
    public Customer signUp(SignUpCommand command) {
        var customer = createCustomer(command);

        customerRepository.save(customer);

        return customer;
    }

    private Customer createCustomer(SignUpCommand command) {
        var email = Email.fromString(command.email());

        if (!uniqueEmailSpecification.isSatisfiedBy(email)) {
            throw new EmailAlreadyExistException("Customer with e-mail " + email + " already exists");
        }

        return Customer.builder()
                .firstname(command.firstname())
                .lastname(command.lastname())
                .birthday(command.birthday())
                .email(email)
                .password(hashedPasswordFactory.createFromPlainPassword(command.password()))
                .build();
    }
}
