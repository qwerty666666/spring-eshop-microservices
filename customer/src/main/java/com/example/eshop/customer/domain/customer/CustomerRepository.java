package com.example.eshop.customer.domain.customer;

import com.example.eshop.customer.domain.customer.Customer.CustomerId;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, CustomerId> {
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(@Param("email") Email email);
}
