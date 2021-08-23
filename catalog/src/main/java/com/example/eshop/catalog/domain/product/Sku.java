package com.example.eshop.catalog.domain.product;

import com.example.eshop.sharedkernel.domain.base.Entity;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * SKU - Stock Keeping Unit. It is distinct item for sale and for
 * inventory management.
 * <p>
 * SKUs are unique identified by {@code EAN}.
 * <p>
 * From the customer's perspective SKU is a single item which can be
 * added to cart. But typically, customers will work with {@link Product}
 * instead - a group of SKU, where each SKU is a product variant with
 * unique attributes like size, color, etc.
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
    @Embedded
    @AttributeOverride(
            name = "ean",
            column = @Column(name = "ean", length = 13, unique = true, nullable = false, updatable = false)
    )
    @NotNull
    private Ean ean;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency")),
    })
    private Money price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    @Override
    public Long id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Sku sku = (Sku) o;

        return Objects.equals(ean, sku.ean);
    }

    @Override
    public int hashCode() {
        return ean.hashCode();
    }
}
