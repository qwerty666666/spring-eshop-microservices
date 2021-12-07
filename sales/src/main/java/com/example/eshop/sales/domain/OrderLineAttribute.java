package com.example.eshop.sales.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Attribute value for {@link OrderLine} (like size, color, etc.).
 * <p>
 * This class is immutable because product data can't be changed
 * after {@link Order} is created.
 */
@Embeddable
@Immutable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class OrderLineAttribute {
    @Column(name = "attribute_id", nullable = false)
    private Long attributeId;
    @Column(name = "value", nullable = false)
    private String value;
    @Column(name = "name", nullable = false)
    private String name;
}
