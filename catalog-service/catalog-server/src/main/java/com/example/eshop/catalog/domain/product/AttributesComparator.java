package com.example.eshop.catalog.domain.product;

import java.util.Comparator;

public class AttributesComparator implements Comparator<AttributeValue> {
    @Override
    public int compare(AttributeValue o1, AttributeValue o2) {
        if (o1.equals(o2)) {
            return 0;
        }

        return o1.getAttribute().getName().compareTo(o2.getAttribute().getName()) > 0 ? 1 : -1;
    }
}
