package com.example.eshop.catalog.domain.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Class represents unique value for {@link Attribute}.
 */
@Entity
@Table(
        name = "attribute_values",
        indexes = { @Index(name = "attribute_values_attribute_id_idx", columnList = "attribute_id") }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttributeValue implements Comparable<AttributeValue> {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "attribute_id", nullable = false)
    @NotNull
    private Attribute attribute;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "sort", nullable = false)
    private int sort;

    public AttributeValue(Attribute attribute, String value, int sort) {
        this.attribute = attribute;
        this.value = value;
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AttributeValue that = (AttributeValue) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return attribute.getName() + " -> " + getValue();
    }

    @Override
    public int compareTo(AttributeValue o) {
        return o.getSort() - getSort();
    }
}
