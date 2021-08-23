package com.example.eshop.catalog.domain.category;

import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.lang.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Category - collection of {@link Product} grouped together.
 * <p>
 * Categories are hierarchical. For simplicity, we use <i>Adjacency List</i> for storing
 * hierarchical structure in relational database.
 */
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
@Getter
public class Category implements AggregateRoot<CategoryId> {
    @EmbeddedId
    @Getter(AccessLevel.NONE)
    private CategoryId id;

    @Column(name = "name", nullable = false)
    @NotEmpty
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private Set<Category> children = new HashSet<>();

    protected Category() {
        this(DomainObjectId.randomId(CategoryId.class));
    }

    protected Category(CategoryId id) {
        this.id = id;
    }

    @Override
    public CategoryId id() {
        return id;
    }

    public void setName(String name) {
        Assertions.notEmpty(name, "Name must be non empty");
        this.name = name;
    }

    public void setParent(@Nullable Category parent) {
        this.parent = parent;
    }

    public void addChild(Category child) {
        Assertions.notNull(child, "Child must be not null");
        this.children.add(child);
    }

    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Category category = (Category) o;

        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CategoryId extends DomainObjectId {
        public CategoryId(String uuid) {
            super(uuid);
        }
    }

    public static class CategoryBuilder {
        @Nullable
        private CategoryId id;
        private String name;
        @Nullable
        private Category parent;
        @Nullable
        private Set<Category> children;

        public CategoryBuilder id(CategoryId id) {
            this.id = id;
            return this;
        }

        public CategoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CategoryBuilder parent(@Nullable Category parent) {
            this.parent = parent;
            return this;
        }

        public CategoryBuilder children(Set<Category> children) {
            this.children = children;
            return this;
        }

        public Category build() {
            var category = (id == null ? new Category() : new Category(id));

            category.setName(name);
            category.setParent(parent);
            if (children != null) {
                this.children.forEach(category::addChild);
            }

            return category;
        }
    }
}
