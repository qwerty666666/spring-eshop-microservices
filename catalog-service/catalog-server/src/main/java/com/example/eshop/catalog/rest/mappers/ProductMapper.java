package com.example.eshop.catalog.rest.mappers;

import com.example.eshop.catalog.client.api.model.PagedProductList;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Mapper(
        componentModel = "spring",
        uses = { EanMapper.class, PageableMapper.class, ImageMapper.class, AttributeMapper.class }
)
public interface ProductMapper {
    com.example.eshop.catalog.client.api.model.Product toProductDto(Product product);

    @Nullable
    default String toString(@Nullable ProductId id) {
        return Optional.ofNullable(id).map(DomainObjectId::toString).orElse(null);
    }

    @Mapping(target = "quantity", source = "availableQuantity")
    com.example.eshop.catalog.client.api.model.Sku toSkuDto(Sku sku);

    @Mapping(target = "items", expression = "java(toProductDtoList(page.get()))")
    @Mapping(target = "pageable", source = ".")
    PagedProductList toPagedProductListDto(Page<Product> page);

    List<com.example.eshop.catalog.client.api.model.Product> toProductDtoList(Stream<Product> products);
}
