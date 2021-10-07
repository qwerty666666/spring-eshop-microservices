package com.example.eshop.cart.domain.cart;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NaturalId;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.MapKey;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "carts",
        indexes = @Index(name = "customerid", columnList = "customer_id", unique = true)
)
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "Cart.items",
                attributeNodes = @NamedAttributeNode("items")
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Cart extends AggregateRoot<Long> implements Cloneable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private Long id;

    @NaturalId
    @Column(name = "customer_id", nullable = false)
    @NotNull
    private String customerId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @MapKey(name = "ean")
    @OrderBy("creationTime")
    private Map<Ean, CartItem> items = new LinkedHashMap<>();

    public Cart(String customerId) {
        Assertions.notEmpty(customerId, "CustomerId must be non empty");
        this.customerId = customerId;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Creates new {@link CartItem} in this cart.
     *
     * @throws CartItemAlreadyExistException if cart item with given EAN already exist in this cart
     */
    public void addItem(Ean ean, Money price, int quantity, String productName) {
        Assertions.notNull(ean, "EAN must be not null");

        if (containsItem(ean)) {
            throw new CartItemAlreadyExistException("Cart Item with ean " + ean + " already exist in cart");
        }

        var item = new CartItem(this, ean, price, quantity, productName);
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
    public CartItem getItem(Ean ean) {
        if (!containsItem(ean)) {
            throw new CartItemNotFoundException(ean, "Cart does not contain CartItem " + ean);
        }

        return items.get(ean);
    }

    /**
     * @return cart items
     */
    public List<CartItem> getItems() {
        return List.copyOf(this.items.values());
    }

    /**
     * @return if cart has no items
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * @return quantity of all {@link CartItem}s in this cart
     */
    public int getTotalItemsQuantity() {
        return items.values().stream().mapToInt(CartItem::getQuantity).sum();
    }

    /**
     * Removes {@link CartItem} from this Cart
     *
     * @throws CartItemNotFoundException if CartItem with given EAN does not exist in this Cart
     */
    public void removeItem(Ean ean) {
        if (!containsItem(ean)) {
            throw new CartItemNotFoundException(ean, "Cart does not contain CartItem " + ean);
        }

        log.info("Remove CartItem " + getItem(ean) + " from Cart");

        items.remove(ean);
    }

    /**
     * Removes all {@link CartItem} from Cart
     */
    public void clear() {
        this.items.clear();

        log.info("Clear cart");
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

    @Override
    public Cart clone() {
        var clone = new Cart(customerId);

        clone.items = items.values().stream()
                .collect(Collectors.toMap(
                        CartItem::getEan,
                        item -> new CartItem(clone, item.getEan(), item.getPrice(), item.getQuantity(), item.getProductName()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return clone;
    }
}
