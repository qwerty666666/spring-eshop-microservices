package com.example.eshop.core.catalog.domain.category;

import com.example.eshop.core.catalog.domain.category.Category.CategoryId;
import com.example.eshop.core.shared.AggregateRoot;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "categories")
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "Category.parent",
                attributeNodes = { @NamedAttributeNode("parent") }
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Category implements AggregateRoot<CategoryId> {
    @EmbeddedId
    @GenericGenerator(
            name = "categoryId_generator",
            strategy = "com.example.eshop.infrastructure.hibernate.generators.ProductIdGenerator"
    )
    @GeneratedValue(generator = "categoryId_generator")
    @Getter(AccessLevel.NONE)
    private CategoryId id;

    @Column(name = "name", nullable = false)
    @lombok.NonNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private Set<Category> children = new HashSet<>();

    @Override
    @Nullable
    public CategoryId id() {
        return id;
    }

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @EqualsAndHashCode
    public static class CategoryId implements Serializable {
        @Column(name = "id", nullable = false)
        private UUID id;

        public CategoryId(UUID uuid) {
            this.id = Objects.requireNonNull(uuid, "uuid must not be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
