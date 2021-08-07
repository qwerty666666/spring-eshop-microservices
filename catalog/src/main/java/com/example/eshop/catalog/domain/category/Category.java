package com.example.eshop.catalog.domain.category;

import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.lang.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
        @GeneratedValue
        @Column(name = "id", nullable = false)
        private Long id;

        public CategoryId(Long id) {
            this.id = Objects.requireNonNull(id, "id must not be null");
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }
}
