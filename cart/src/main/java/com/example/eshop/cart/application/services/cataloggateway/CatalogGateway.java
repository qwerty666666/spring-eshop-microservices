package com.example.eshop.cart.application.services.cataloggateway;

import com.example.eshop.catalog.client.api.model.Product;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import java.util.List;
import java.util.Map;

/**
 * Gateway to Catalog microservice
 */
public interface CatalogGateway {
    // TODO through exceptions if service is unavailable ??
    /**
     * Returns product by given EAN
     *
     * @throws ProductNotFoundException if Product with given EAN does not exist
     */
    Product getProductByEan(Ean ean);

    /**
     * Returns products by given EANs. If for any EAN there is no product
     * then this EAN will be mapped to null
     */
    Map<Ean, Product> getProductsByEan(List<Ean> ean);
}
