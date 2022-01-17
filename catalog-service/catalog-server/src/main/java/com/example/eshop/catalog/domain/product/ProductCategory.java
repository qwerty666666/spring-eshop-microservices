package com.example.eshop.catalog.domain.product;

import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Join-table for products-categories relationship
 *
 * @see Product
 * @see Category
 */
@Entity
@Table(
        name = "products_categories",
        indexes = {
                @Index(name = "products_categories_category_idx", columnList = "category_id"),
                @Index(name = "products_categories_product_idx", columnList = "product_id"),
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategory {
    @EmbeddedId
    private Id id;

    // Use unidirectional association for categories to avoid populating
    // category.products list. And use cascade delete in DDL
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @CreationTimestamp
    @NotNull
    private LocalDateTime addedOn;

    /**
     * @return {@link CategoryId} from association
     */
    public CategoryId getCategoryId() {
        return id.categoryId;
    }

    /**
     * @return {@link ProductId} from association
     */
    public ProductId getProductId() {
        return id.productId;
    }

    /**
     * @return datetime when product was added to category
     */
    public LocalDateTime getAddedOn() {
        return addedOn;
    }

    /**
     * @return {@link Category} from association
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @return {@link Product} from association
     */
    public Product getProduct() {
        return product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProductCategory that = (ProductCategory) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * Primary Key of {@link ProductCategory}.
     * <p>
     * (Category, Product) tuple is unique per table.
     */
    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    private static class Id implements Serializable {
        @AttributeOverride(name = "uuid", column = @Column(name = "category_id", updatable = false, insertable = false))
        private CategoryId categoryId;
        @AttributeOverride(name = "uuid", column = @Column(name = "product_id", updatable = false, insertable = false))
        private ProductId productId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
            Id id = (Id) o;
            return categoryId != null && Objects.equals(categoryId, id.categoryId)
                    && productId != null && Objects.equals(productId, id.productId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(categoryId, productId);
        }
    }
}
