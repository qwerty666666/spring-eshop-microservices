package com.example.eshop.catalog.rest.mappers;

import com.example.eshop.catalog.client.model.CategoryDto;
import com.example.eshop.catalog.client.model.CategoryTreeItemDto;
import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parent")
    CategoryDto toCategoryDto(Category category);

    default String getCategoryId(Category category) {
        if (category == null) {
            return null;
        }
        return Optional.ofNullable(category.getId())
                .map(DomainObjectId::toString)
                .orElse(null);
    }

    default String toString(CategoryId id) {
        return id == null ? null : id.toString();
    }

    List<CategoryDto> toCategoryDtoList(List<Category> categories);

    CategoryTreeItemDto toCategoryTreeItem(Category category);

    List<CategoryTreeItemDto> toTree(List<Category> categories);
}
