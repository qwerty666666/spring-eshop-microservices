package com.example.eshop.rest.mappers;

import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.rest.dto.CategoryDto;
import com.example.eshop.rest.dto.CategoryTreeItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parent.id")
    CategoryDto toCategoryDto(Category category);

    default String toString(CategoryId id) {
        return id == null ? null : id.toString();
    }

    List<CategoryDto> toCategoryDtoList(List<Category> categories);

    CategoryTreeItemDto toCategoryTreeItemDto(Category category);

    List<CategoryTreeItemDto> toTree(List<Category> categories);
}
