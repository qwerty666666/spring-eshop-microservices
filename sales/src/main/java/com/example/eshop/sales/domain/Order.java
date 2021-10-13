package com.example.eshop.sales.domain;

import com.example.eshop.sharedkernel.domain.Assertions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderColumn(name = "sort")
    @NotEmpty
    private List<OrderLine> lines = new ArrayList<>();

    @Embedded
    private Delivery delivery;

    @Embedded
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderStatus status;

    public Order(UUID id, String customerId, Delivery delivery, Payment payment, List<OrderLine> lines) {
        Assertions.notNull(id, "id must be not null");
        Assertions.notEmpty(customerId, "customerId must be not empty");
        Assertions.notEmpty(lines, "OrderLines must be not empty");
        Assertions.notNull(delivery, "delivery must be not null");
        Assertions.notNull(payment, "payment must be not null");

        this.id = id;
        this.customerId = customerId;
        this.delivery = delivery;
        this.payment = payment;

        lines.forEach(this::addLine);
    }

    public void setStatus(OrderStatus status) {
        Assertions.notNull(status, "Status must be not null");

        this.status = status;
    }

    public void addLine(OrderLine line) {
        Assertions.notNull(line, "line must be not null");

        lines.add(line);
        line.setOrder(this);
    }

    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Order order = (Order) o;
        return id != null && Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
