package com.example.eshop.catalog.domain.product;

import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Join-table for products-categories relationship
 */
@Entity
@Table(
        name = "products_categories",
        indexes = @Index(columnList = "category_id")
)
public class ProductCategory {
    @EmbeddedId
    private Id id;

    // Use unidirectional association for categories to avoid populating
    // category.products list, so use cascade delete in ddl
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @CreationTimestamp
    private LocalDateTime addedOn;

    public CategoryId getCategoryId() {
        return id.categoryId;
    }

    public ProductId getProductId() {
        return id.productId;
    }

    /**
     * @return datetime when product was added to category
     */
    public LocalDateTime getAddedOn() {
        return addedOn;
    }

    public Category getCategory() {
        return category;
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        ProductCategory productCategory = (ProductCategory) o;

        return Objects.equals(id, productCategory.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id implements Serializable {
        @AttributeOverride(name = "id", column = @Column(name = "category_id"))
        private CategoryId categoryId;
        @AttributeOverride(name = "id", column = @Column(name = "product_id"))
        private ProductId productId;
    }
}
