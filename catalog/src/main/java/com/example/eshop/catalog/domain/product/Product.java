package com.example.eshop.catalog.domain.product;

import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Getter
public class Product implements AggregateRoot<ProductId> {
    @EmbeddedId
    @Getter(AccessLevel.NONE)
    private ProductId id;

    @Column(name = "name", nullable = false)
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
    public ProductId id() {
        return id;
    }

    public void setName(String name) {
        Assertions.notEmpty(name, "Name must be non empty");
        this.name = name;
    }

    public void addSku(Sku sku) {
        Assertions.notNull(sku, "SKU must be not null");
        this.sku.add(sku);
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
