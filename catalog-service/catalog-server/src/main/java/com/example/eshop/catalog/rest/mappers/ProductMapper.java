package com.example.eshop.catalog.rest.mappers;

import com.example.eshop.catalog.client.api.model.PagedProductListDto;
import com.example.eshop.catalog.client.api.model.ProductDto;
import com.example.eshop.catalog.client.api.model.ProductWithSkuDto;
import com.example.eshop.catalog.client.api.model.SkuDto;
import com.example.eshop.catalog.client.api.model.SkuInfoDto;
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
    ProductDto toProductDto(Product product);

    ProductWithSkuDto toProductWithSkuDto(Product product);

    @Nullable
    default String toString(@Nullable ProductId id) {
        return Optional.ofNullable(id).map(DomainObjectId::toString).orElse(null);
    }

    @Mapping(target = "productId", source = "sku.product.id")
    @Mapping(target = "quantity", source = "availableQuantity")
    SkuDto toSkuDto(Sku sku);

    @Mapping(target = "items", expression = "java(toProductDtoList(page.get()))")
    @Mapping(target = "pageable", source = ".")
    PagedProductListDto toPagedProductListDto(Page<Product> page);

    List<ProductWithSkuDto> toProductDtoList(Stream<Product> products);

    /**
     * Builds {@link SkuInfoDto} from products founded for given EAN list
     *
     * @param skuEans EANs of sku that should be in the result
     * @param products products which have sku for {@code skuEans}
     */
    default SkuInfoDto toSkuList(List<Ean> skuEans, List<Product> products) {
        var productMap = new HashMap<String, ProductDto>();
        var skuList = new ArrayList<SkuDto>();

        var eanSet = new HashSet<>(skuEans);

        for (var product: products) {
            for (var sku: product.getSku()) {
                if (eanSet.contains(sku.getEan())) {
                    skuList.add(toSkuDto(sku));
                    productMap.put(product.getId().toString(), toProductDto(product));
                }
            }
        }

        return new SkuInfoDto(productMap, skuList);
    }
}
