package com.example.eshop.catalog.domain.product;

import java.util.Comparator;

/**
 * Compare {@link Sku} by first attribute.
 * <p>
 * SKU must have the same attributes list. If it is not,
 * SKU won't be sorted.
 */
public class ByFirstAttributeSkuComparator implements Comparator<Sku> {
    @Override
    public int compare(Sku a, Sku b) {
        if (a.getAttributeList().isEmpty()) {
            return 0;
        }

        // we assume that SKU must have the same attributes
        if (!a.getAttributeList().equals(b.getAttributeList())) {
            return 0;
        }

        return a.getAttributeValues().get(0).getSort() - b.getAttributeValues().get(0).getSort();
    }
}
