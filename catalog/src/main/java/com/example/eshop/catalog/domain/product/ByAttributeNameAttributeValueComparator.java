package com.example.eshop.catalog.domain.product;

import java.util.Comparator;

/**
 * Compare {@link AttributeValue} by theirs {@link Attribute} Name
 */
public class ByAttributeNameAttributeValueComparator implements Comparator<AttributeValue> {
    @Override
    public int compare(AttributeValue a, AttributeValue b) {
        return a.getAttribute().getName().compareTo(b.getAttribute().getName());
    }
}
