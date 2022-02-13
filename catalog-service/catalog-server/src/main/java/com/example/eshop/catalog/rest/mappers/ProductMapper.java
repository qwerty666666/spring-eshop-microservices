package com.example.eshop.catalog.rest.mappers;

import com.example.eshop.catalog.client.api.model.PagedProductList;
import com.example.eshop.catalog.client.api.model.ProductWithSku;
import com.example.eshop.catalog.client.api.model.SkuInfo;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Mapper(
        componentModel = "spring",
        uses = { EanMapper.class, PageableMapper.class, ImageMapper.class, AttributeMapper.class }
)
public interface ProductMapper {
    com.example.eshop.catalog.client.api.model.Product toProductDto(Product product);

    ProductWithSku toProductWithSkuDto(Product product);

    @Nullable
    default String toString(@Nullable ProductId id) {
        return Optional.ofNullable(id).map(DomainObjectId::toString).orElse(null);
    }

    @Mapping(target = "productId", source = "sku.product.id")
    @Mapping(target = "quantity", source = "availableQuantity")
    com.example.eshop.catalog.client.api.model.Sku toSkuDto(Sku sku);

    @Mapping(target = "items", expression = "java(toProductDtoList(page.get()))")
    @Mapping(target = "pageable", source = ".")
    PagedProductList toPagedProductListDto(Page<Product> page);

    List<ProductWithSku> toProductDtoList(Stream<Product> products);

    /**
     * Builds {@link SkuInfo} from products founded for given EAN list
     *
     * @param skuEans EANs of sku that should be in the result
     * @param products products which have sku for {@code skuEans}
     */
    default SkuInfo toSkuList(List<Ean> skuEans, List<Product> products) {
        var productMap = new HashMap<String, com.example.eshop.catalog.client.api.model.Product>();
        var skuList = new ArrayList<com.example.eshop.catalog.client.api.model.Sku>();

        var eanSet = new HashSet<>(skuEans);

        for (var product: products) {
            for (var sku: product.getSku()) {
                if (eanSet.contains(sku.getEan())) {
                    skuList.add(toSkuDto(sku));
                    productMap.put(product.getId().toString(), toProductDto(product));
                }
            }
        }

        return new SkuInfo(productMap, skuList);
    }
}
