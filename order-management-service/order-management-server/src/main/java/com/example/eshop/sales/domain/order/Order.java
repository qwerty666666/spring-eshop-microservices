package com.example.eshop.sales.domain.order;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Class represents Order. Order contains list of {@link OrderLine}
 * each of which provides info about products in Order.
 * <p>
 * Order can have different {@link OrderStatus}, but we use only PENDING
 * status.
 */
@Entity
@Table(
        name = "orders",
        indexes = @Index(name = "orders_customer_id_idx", columnList = "customer_id")
)
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Slf4j
public class Order extends AggregateRoot<UUID> {
    @Id
    @Column(name = "id", nullable = false)
    @Type(type="org.hibernate.type.PostgresUUIDType")
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderColumn(name = "sort")
    @NotEmpty
    private final List<OrderLine> lines = new ArrayList<>();

    @Embedded
    @NotNull
    private Delivery delivery;

    @Embedded
    @NotNull
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    @NotNull
    private OrderStatus status;

    @NotNull
    private LocalDateTime creationDate;

    /**
     * Creates new Order and set PENDING status.
     */
    public Order(UUID id, String customerId, Delivery delivery, Payment payment, LocalDateTime creationDate, List<OrderLine> lines) {
        Assertions.notNull(id, "id must be not null");
        Assertions.notEmpty(customerId, "customerId must be not empty");
        Assertions.notEmpty(lines, "orderLines must be not empty");
        Assertions.notNull(delivery, "delivery must be not null");
        Assertions.notNull(payment, "payment must be not null");
        Assertions.notNull(creationDate, "creationDate must be not null");

        this.id = id;
        this.customerId = customerId;
        this.delivery = delivery;
        this.payment = payment;
        this.creationDate = creationDate;

        lines.forEach(this::addLine);

        setStatus(OrderStatus.PENDING);
    }

    @Override
    public UUID getId() {
        return id;
    }

    private void setStatus(OrderStatus status) {
        this.status = status;

        log.info("Status changed " + status);
    }

    /**
     * Adds new {@link OrderLine} to this Order
     */
    public void addLine(OrderLine line) {
        Assertions.notNull(line, "line must be not null");

        lines.add(line);
        line.setOrder(this);
    }

    /**
     * @return unmodifiable List of {@link OrderLine} in order they were added to this Order
     */
    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    /**
     * @return total price of all {@link OrderLine}s
     */
    public Money getCartPrice() {
        return lines.stream().map(OrderLine::getPrice).reduce(Money.ZERO, Money::add);
    }

    /**
     * @return total price of order, i.e. {@code delieryPrice + cartPrice}
     */
    public Money getPrice() {
        return delivery.getPrice().add(getCartPrice());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Order order = (Order) o;
        return id != null && Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
