package com.example.eshop.core.catalog.domain.category;

import com.example.eshop.core.catalog.domain.category.Category.CategoryId;
import com.example.eshop.core.shared.AggregateRoot;
import lombok.*;
import org.hibernate.Hibernate;
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
        ),
        @NamedEntityGraph(
                name = "Category.children",
                attributeNodes = { @NamedAttributeNode("children") }
        )
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Category implements AggregateRoot<CategoryId> {
    @EmbeddedId
    @GenericGenerator(
            name = "category_id_generator",
            strategy = "com.example.eshop.infrastructure.hibernate.generators.ProductIdGenerator"
    )
    @GeneratedValue(generator = "category_id_generator")
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
    public CategoryId id() {
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

        Category category = (Category) o;

        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
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
