package com.example.eshop.sales.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @EntityGraph(attributePaths = "lines")
    Page<Order> findOrdersWithLinesByCustomerId(String customerId, Pageable pageable);

    @EntityGraph(attributePaths = "lines")
    Optional<Order> findOrderWithLinesById(UUID id);
}
