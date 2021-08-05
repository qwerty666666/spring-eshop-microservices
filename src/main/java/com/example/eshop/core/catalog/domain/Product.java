package com.example.eshop.core.catalog.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import com.example.eshop.core.shared.AggregateRoot;
import lombok.*;
import javax.persistence.*;
import com.example.eshop.core.catalog.domain.Product.ProductId;
import org.hibernate.annotations.GenericGenerator;
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
    @GenericGenerator(
            name = "productId_generator",
            strategy = "com.example.eshop.infrastructure.hibernate.generators.ProductIdGenerator"
    )
    @GeneratedValue(generator = "productId_generator")
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

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @EqualsAndHashCode
    public static class ProductId implements Serializable {
        @Column(name = "id", nullable = false)
        private UUID id;

        public ProductId(UUID uuid) {
            this.id = Objects.requireNonNull(uuid, "uuid must not be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }

    @Override
    public ProductId id() {
        return id;
    }
}
