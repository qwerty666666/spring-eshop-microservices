package com.example.eshop.catalog.domain.product;

import com.example.eshop.sharedkernel.domain.base.Entity;
import com.example.eshop.sharedkernel.domain.financial.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Stock Keeping Unit
 */
@javax.persistence.Entity
@Table(name = "sku")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Sku implements Entity<Long> {
    @Id
    @GeneratedValue
    @Column(name = "id")
    @Getter(AccessLevel.NONE)
    private Long id;

    @NaturalId
    @Column(name = "ean", length = 13, unique = true, nullable = false, updatable = false)
    @NotNull
    @Length(min = 13, max = 13)
    private String ean;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency")),
    })
    private Money price;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;

    @Override
    public Long id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        Sku sku = (Sku) o;

        return Objects.equals(id, sku.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
