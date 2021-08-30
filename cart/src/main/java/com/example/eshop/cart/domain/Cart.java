package com.example.eshop.cart.domain;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NaturalId;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(
        name = "carts",
        indexes = @Index(name = "customerid", columnList = "customer_id", unique = true)
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Cart extends AggregateRoot<Long> {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private Long id;

    @NaturalId
    @Column(name = "customer_id", nullable = false)
    @NotNull
    private String customerId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.PERSIST)
    @MapKey(name = "ean")
    @OrderBy("creationTime")
    private Map<Ean, CartItem> items = new LinkedHashMap<>();

    public Cart(String customerId) {
        Assertions.notEmpty(customerId, "CustomerId must be non empty");
        this.customerId = customerId;
    }

    @Override
    public Long id() {
        return id;
    }

    /**
     * Creates new {@link CartItem} in this cart.
     *
     * @throws CartItemAlreadyExistException if cart item with given EAN already exist in this cart
     */
    public void addItem(Ean ean, int quantity) {
        Assertions.notNull(ean, "EAN must be not null");

        if (containsItem(ean)) {
            throw new CartItemAlreadyExistException("Cart Item with ean " + ean + " already exist in cart");
        }

        var item = new CartItem(this, ean, quantity);
        items.put(ean, item);

        log.info("New CartItem created " + item);
    }

    /**
     * Change quantity for {@link CartItem} with given {@code ean}
     *
     * @throws CartItemNotFoundException if cart does not contain item with specified ean
     */
    public void changeItemQuantity(Ean ean, int quantity) {
        Assertions.notNull(ean, "EAN must be not null");

        var existedItem = getItem(ean);
        existedItem.setQuantity(quantity);
    }

    /**
     * @return true if cart contains {@link CartItem} with given {@code eam}
     */
    public boolean containsItem(Ean ean) {
        return items.containsKey(ean);
    }

    /**
     * @throws CartItemNotFoundException if item with given {@code ean} does not exist in this cart
     */
    private CartItem getItem(Ean ean) {
        if (!containsItem(ean)) {
            throw new CartItemNotFoundException("Cart does not contain CartItem " + ean);
        }

        return items.get(ean);
    }

    /**
     * @return cart items
     */
    public Collection<CartItem> getItems() {
        return Collections.unmodifiableCollection(this.items.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Cart cart = (Cart) o;

        return Objects.equals(customerId, cart.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customerId);
    }
}
