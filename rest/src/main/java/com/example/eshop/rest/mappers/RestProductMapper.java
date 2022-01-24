package com.example.eshop.rest.mappers;

import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.rest.dto.PagedProductListDto;
import com.example.eshop.rest.dto.ProductDto;
import com.example.eshop.rest.dto.SkuDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import java.util.List;
import java.util.stream.Stream;

@Mapper(
        componentModel = "spring",
        uses = { RestEanMapper.class, RestPageableMapper.class, RestImageMapper.class, RestAttributeMapper.class }
)
public interface RestProductMapper {
    ProductDto toProductDto(Product product);

    @Nullable
    default String toString(@Nullable ProductId id) {
        return id == null ? null : id.toString();
    }

    @Mapping(target = "quantity", source = "availableQuantity")
    SkuDto toSkuDto(Sku sku);

    @Mapping(target = "items", expression = "java(toProductDtoList(page.get()))")
    @Mapping(target = "pageable", source = ".")
    PagedProductListDto toPagedProductListDto(Page<Product> page);

    List<ProductDto> toProductDtoList(Stream<Product> products);
}
