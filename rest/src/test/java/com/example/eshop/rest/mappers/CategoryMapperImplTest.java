package com.example.eshop.rest.mappers;

import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.rest.config.MappersConfig;
import com.example.eshop.rest.dto.CategoryDto;
import com.example.eshop.rest.dto.CategoryTreeItemDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MappersConfig.class)
class CategoryMapperImplTest {
    @Autowired
    CategoryMapper mapper;

    @Test
    void testToCategoryDto() {
        // Given
        var category = createCategory();

        // When
        var dto = mapper.toCategoryDto(category);
        
        // Then
        assertCategoriesEquals(category, dto);
    }

    @Test
    void testToCategoryTreeItemDto() {
        // Given
        var category = createCategory();

        // When
        var dto = mapper.toCategoryTreeItemDto(category);

        // Then
        assertCategoryTreeEquals(category, dto);
    }

    private Category createCategory() {
        var parent = Category.builder().id(new CategoryId("1")).name("parent").build();
        var child1 = Category.builder().id(new CategoryId("2")).parent(parent).name("child 1").build();
        var child2 = Category.builder().id(new CategoryId("3")).parent(parent).name("child 2").build();

        parent.addChild(child1);
        parent.addChild(child2);

        return parent;
    }

    private static void assertCategoriesEquals(Category category, CategoryDto categoryDto) {
        assertThat(categoryDto.getId()).as("ID").isEqualTo(category.getId().toString());
        assertThat(categoryDto.getName()).as("Name").isEqualTo(category.getName());

        var expectedParentId = category.getParent() == null ? null : category.getParent().getId().toString();
        assertThat(categoryDto.getParentId()).as("Parent ID").isEqualTo(expectedParentId);
    }

    private static void assertCategoryTreeEquals(Category category, CategoryTreeItemDto treeItemDto) {
        assertThat(treeItemDto.getId()).as("ID").isEqualTo(category.getId().toString());
        assertThat(treeItemDto.getName()).as("Name").isEqualTo(category.getName());

        Assertions.assertListEquals(category.getChildren(), treeItemDto.getChildren(), CategoryMapperImplTest::assertCategoryTreeEquals);
    }
}
