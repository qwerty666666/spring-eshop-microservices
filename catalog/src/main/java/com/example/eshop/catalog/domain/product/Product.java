package com.example.eshop.catalog.domain.product;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import lombok.*;
import javax.persistence.*;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "products")
@NamedEntityGraph(
        name = "Product.sku",
        attributeNodes = @NamedAttributeNode("sku")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Product implements AggregateRoot<ProductId> {
    @EmbeddedId
    @Getter(AccessLevel.NONE)
    private ProductId id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Sku> sku = new HashSet<>();

    @OneToMany(mappedBy = "product")
    // Use on cascade delete in ddl instead of CascadeType.DELETE to avoid N requests
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    private Set<ProductCategory> categories = new HashSet<>();

    @Override
    public ProductId id() {
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

        Product product = (Product) o;

        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @EqualsAndHashCode
    public static class ProductId implements Serializable {
        @Column(name = "id", nullable = false)
        @GeneratedValue
        private Long id;

        public ProductId(Long id) {
            this.id = Objects.requireNonNull(id, "id must not be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
