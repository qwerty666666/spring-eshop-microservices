package com.example.eshop.cart.domain.cart;

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
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class represents single item in {@link Cart}.
 */
@javax.persistence.Entity
@Table(
        name = "cart_items",
        indexes = {
                @Index(name = "cart_items_cart_id_idx", columnList = "cart_id"),
                @Index(name = "cart_items_uniq_item", columnList = "cart_id, ean", unique = true),
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Slf4j
@ToString(onlyExplicitlyIncluded = true)
public class CartItem implements Entity<Long> {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    @ToString.Include
    private Long id;

    @Embedded
    @AttributeOverride(name = "ean", column = @Column(name = "ean", nullable = false))
    @NotNull
    @ToString.Include
    private Ean ean;

    @Column(name = "quantity", nullable = false)
    @Positive
    @ToString.Include
    private int quantity;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price", nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false))
    @NotNull
    private Money price;

    @Column(name = "product_name", nullable = false)
    @NotEmpty
    private String productName;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "create_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime creationTime;

    CartItem(Cart cart, Ean ean, Money price, int quantity, String productName) {
        Assertions.notNull(cart, "cart must be positive number");
        Assertions.notNull(ean, "EAN must be not null");
        Assertions.notNull(price, "price must be not null");
        Assertions.positive(quantity, "quantity must be positive number");
        Assertions.notEmpty(productName, "productName must be non empty");

        this.cart = cart;
        this.ean = ean;
        this.price = price;
        this.quantity = quantity;
        this.productName = productName;
    }

    /**
     * Set quantity.
     * <p>
     * This method must be called only through {@link Cart} Aggregate Root.
     */
    void setQuantity(int quantity) {
        Assertions.positive(quantity, "Quantity should be positive number");

        this.quantity = quantity;

        log.info("CartItem quantity changed" + this);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CartItem other = (CartItem) o;

        return Objects.equals(cart, other.cart) && Objects.equals(ean, other.ean);
    }

    @Override
    public int hashCode() {
        return ean.hashCode();
    }
}
