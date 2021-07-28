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
import org.springframework.lang.NonNull;

@Entity
@Table(name = "products")
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

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @EqualsAndHashCode
    public static class ProductId implements Serializable {
        @Column(name = "id", nullable = false)
        private UUID id;

        public ProductId(@NonNull UUID uuid) {
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
