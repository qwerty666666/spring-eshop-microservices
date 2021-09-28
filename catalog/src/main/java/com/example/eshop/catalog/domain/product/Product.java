package com.example.eshop.catalog.domain.product;

import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Product is a group of {@link Sku}, where each SKU is a distinct
 * product variant with unique attribute set (like size, color, etc.).
 * <p>
 * From catalog perspective, users will work with {@code Product}
 * instead of {@code SKU}.
 */
@Entity
@Table(name = "products")
@NamedEntityGraph(
        name = "Product.sku",
        attributeNodes = @NamedAttributeNode("sku")
)
@ToString(onlyExplicitlyIncluded = true)
@Slf4j
public class Product extends AggregateRoot<ProductId> {
    @EmbeddedId
    @ToString.Include
    private ProductId id;

    @Column(name = "name", nullable = false)
    @ToString.Include
    @Getter
    private String name;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Sku> sku = new HashSet<>();

    @OneToMany(mappedBy = "product")
    // Use on cascade delete in ddl instead of CascadeType.DELETE to avoid N requests
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<ProductCategory> categories = new HashSet<>();

    protected Product() {
        this(DomainObjectId.randomId(ProductId.class));
    }

    protected Product(ProductId id) {
        this.id = id;
    }

    @Override
    public ProductId getId() {
        return id;
    }

    public void setName(String name) {
        Assertions.notEmpty(name, "Name must be non empty");
        this.name = name;
    }

    /**
     * Set available quantity for SKU with given {@code ean}
     *
     * @throws SkuNotFoundException if SKU with given {@code ean} not found in this Product
     */
    public void setSkuAvailableQuantity(Ean ean, int availableQuantity) {
        Assertions.notNull(ean, "EAN must be non empty");
        getSku(ean).setAvailableQuantity(availableQuantity);
    }

    /**
     * @throws SkuNotFoundException if SKU with given {@code ean} not found in this Product
     */
    public Sku getSku(Ean ean) {
        return this.sku.stream()
                .filter(sku -> sku.getEan().equals(ean))
                .findFirst()
                .orElseThrow(() -> new SkuNotFoundException("SKU " + ean + " does not exist in Product " + this));
    }

    public Set<Sku> getSku() {
        return Collections.unmodifiableSet(sku);
    }

    /**
     * Add new SKU to this Product
     */
    public void addSku(Ean ean, Money price, int quantity) {
        var sku = new Sku(this, ean, price, quantity);

        this.sku.add(sku);

        log.info("Add new SKU " + sku);
    }

    public Set<ProductCategory> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Product product = (Product) o;

        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProductId extends DomainObjectId {
        public ProductId(String uuid) {
            super(uuid);
        }
    }

    public static class ProductBuilder {
        @Nullable
        private ProductId id;
        private String name;

        public ProductBuilder id(ProductId id) {
            this.id = id;
            return this;
        }

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public Product build() {
            var product = (id == null ? new Product() : new Product(id));
            product.setName(name);

            return product;
        }
    }
}
