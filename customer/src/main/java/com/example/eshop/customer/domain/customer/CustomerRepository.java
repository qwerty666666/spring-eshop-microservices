package com.example.eshop.customer.domain.customer;

import com.example.eshop.customer.domain.customer.Customer.CustomerId;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, CustomerId> {
    /**
     * Finds {@link Customer} by given {@code email}.
     */
    Optional<Customer> findByEmail(Email email);

    /**
     * Check if {@link Customer} with given {@code email} exists.
     */
    boolean existsByEmail(@Param("email") Email email);
}
