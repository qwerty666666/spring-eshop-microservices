package com.example.eshop.catalog.domain.product;

import java.util.Comparator;

/**
 * Compare {@link Sku} by first attribute.
 * <p>
 * SKU must have the same attributes list. If it is not,
 * SKU won't be sorted.
 */
public class ByAttributeValueSkuComparator implements Comparator<Sku> {
    @Override
    public int compare(Sku a, Sku b) {
        if (a.getAttributeList().isEmpty()) {
            return 0;
        }

        // we assume that SKU must have the same attributes
        if (!a.getAttributeList().equals(b.getAttributeList())) {
            return 0;
        }

        var aAttributeValue = a.getAttributeValues().get(0).getValue();
        var bAttributeValue = b.getAttributeValues().get(0).getValue();

        return aAttributeValue.compareTo(bAttributeValue);
    }
}
