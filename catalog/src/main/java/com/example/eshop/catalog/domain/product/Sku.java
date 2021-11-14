package com.example.eshop.catalog.domain.product;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.Entity;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SortNatural;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * SKU - Stock Keeping Unit. It is distinct item for sale and for
 * inventory management.
 * <p>
 * SKUs are unique identified by {@code EAN}.
 * <p>
 * From the customer's perspective SKU is a single item which can be
 * added to cart. But typically, customers will work with {@link Product}
 * instead - a group of SKU, where each SKU is a product variant with
 * unique attributes set {@link Attribute} like size, color, etc.
 */
@javax.persistence.Entity
@Table(
        name = "sku",
        indexes = { @Index(name = "sku_product_id_idx", columnList = "product_id") }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(onlyExplicitlyIncluded = true)
@Slf4j
public class Sku implements Entity<Long> {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    @ToString.Include
    private Long id;

    @NaturalId
    @Embedded
    @AttributeOverride(
            name = "ean",
            column = @Column(name = "ean", length = 13, unique = true, nullable = false, updatable = false)
    )
    @NotNull
    @ToString.Include
    private Ean ean;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false))
    @NotNull
    private Money price;

    @Column(name = "available_quantity", nullable = false)
    @PositiveOrZero
    private int availableQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    @ManyToMany(
            // We use Eager-loading strategy there. It is bad practice,
            // but it is appropriate for our case. See Product::getSku for details.
            fetch = FetchType.EAGER,
            cascade = { CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }
    )
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
            name = "sku_attributes",
            joinColumns = @JoinColumn(name = "sku_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_value_id")
    )
    @SortNatural
    private SortedSet<AttributeValue> attributes = new TreeSet<>();

    private Sku(SkuBuilder builder) {
        this.setEan(builder.ean);
        this.setPrice(builder.price);
        this.setAvailableQuantity(builder.availableQuantity);
        this.setAttributes(builder.attributes);
    }

    @Override
    public Long getId() {
        return id;
    }

    private void setEan(Ean ean) {
        Assertions.notNull(ean, "ean must be not null");

        this.ean = ean;
    }

    private void setPrice(Money price) {
        Assertions.notNull(price, "price must be not null");

        this.price = price;
    }

    private void setAttributes(List<AttributeValue> attributes) {
        Assertions.notNull(attributes, "attributes must be not null");

        this.attributes.clear();
        this.attributes.addAll(attributes);
    }

    /**
     * Sets Product for this SKU.
     * <p>
     * This method must be called only through {@link Product} Aggregate Root.
     */
    void setProduct(Product product) {
        Assertions.notNull(product, "product must be not null");

        this.product = product;
    }

    /**
     * Updated available quantity.
     * <p>
     * This method must be called only through {@link Product} Aggregate Root.
     */
    void changeAvailableQuantity(int availableQuantity) {
        setAvailableQuantity(availableQuantity);

        log.info("SKU: " + this + ". Available quantity changed to " + availableQuantity);
    }

    private void setAvailableQuantity(int availableQuantity) {
        Assertions.nonNegative(availableQuantity, "Available quantity can't be negative");

        this.availableQuantity = availableQuantity;

        log.info("SKU: " + this + ". Available quantity changed to " + availableQuantity);
    }

    /**
     * @return if SKU is in stock
     */
    public boolean isAvailable() {
        return availableQuantity > 0;
    }

    /**
     * @return unmodifiable List of SKU {@link AttributeValue} of this SKU
     */
    public List<AttributeValue> getAttributeValues() {
        return List.copyOf(attributes);
    }

    /**
     * @return unmodifiable List of {@link Attribute} of this SKU
     */
    public List<Attribute> getAttributeList() {
        return getAttributeValues().stream().map(AttributeValue::getAttribute).toList();
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

    /**
     * @return new {@link SkuBuilder} instance
     */
    public static SkuBuilder builder() {
        return new SkuBuilder();
    }

    /**
     * Builder for {@link Sku}
     */
    public static class SkuBuilder {
        private Ean ean;
        private Money price;
        private int availableQuantity;
        private List<AttributeValue> attributes = new ArrayList<>();

        public SkuBuilder ean(Ean ean) {
            this.ean = ean;
            return this;
        }

        public SkuBuilder price(Money price) {
            this.price = price;
            return this;
        }

        public SkuBuilder availableQuantity(int availableQuantity) {
            this.availableQuantity = availableQuantity;
            return this;
        }

        public SkuBuilder attributes(List<AttributeValue> attributes) {
            this.attributes = attributes;
            return this;
        }

        public SkuBuilder addAttribute(AttributeValue attributeValue) {
            this.attributes.add(attributeValue);
            return this;
        }

        public Sku build() {
            return new Sku(this);
        }
    }
}
