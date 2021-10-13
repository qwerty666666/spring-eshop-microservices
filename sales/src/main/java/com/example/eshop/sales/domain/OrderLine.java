package com.example.eshop.sales.domain;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Positive;
import java.util.Objects;

@Entity
@Table(name = "order_lines")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderLine {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Embedded
    private Ean ean;

    @Column(name = "quantity", nullable = false)
    @Positive
    private int quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "price_currency", nullable = false))
    })
    private Money itemPrice;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    // TODO Attributes


    public OrderLine(Ean ean, int quantity, Money itemPrice, String productName) {
        Assertions.notNull(ean, "ean must be not null");
        Assertions.positive(quantity, "quantity must be positive");
        Assertions.notNull(itemPrice, "itemPrice must be not null");
        Assertions.notEmpty(productName, "productName must be not empty");

        this.ean = ean;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
        this.productName = productName;
    }

    void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderLine orderLine = (OrderLine) o;
        return id != null && Objects.equals(id, orderLine.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.intValue();
    }
}
