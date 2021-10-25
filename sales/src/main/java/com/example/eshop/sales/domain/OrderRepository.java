package com.example.eshop.sales.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query(value = "select distinct o from Order o join fetch o.lines l where o.customerId = :customerId",
            countQuery = "select count(o) from Order o where o.customerId = :customerId")
    Page<Order> findByCustomerIdWithOrderLines(String customerId, Pageable pageable);
}
