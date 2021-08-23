package com.example.eshop.rest.resources;

import com.example.eshop.rest.resources.shared.PagedResource;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PageableResponseTest {
    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void constructorFillsParametersCorrectly() {
        // given
        var items = List.of(1, 2, 3, 4, 5);
        int pageNumber = 1;
        int pageSize = 2;
        var itemsOnPage = items.subList(pageNumber * pageSize, (pageNumber + 1) * pageSize);
        var page = new PageImpl<>(itemsOnPage, Pageable.ofSize(pageSize).withPage(pageNumber), items.size());

        var converter = mock(Function.class);

        // when
        var sut = new PagedResource(page, converter);

        // then
        for (var i : itemsOnPage) {
            verify(converter).apply(i);
        }

        assertAll(
                () -> assertThat(sut.page).as("page should start from 1").isEqualTo(pageNumber + 1),
                () -> assertThat(sut.totalPages).as("totalPages")
                        .isEqualTo((int) Math.ceil((double) items.size() / pageSize)),
                () -> assertThat(sut.perPage).as("perPage").isEqualTo(page.getSize()),
                () -> assertThat(sut.totalItems).as("totalItems").isEqualTo(items.size()),
                () -> assertThat(sut.items).as("items").hasSameSizeAs(itemsOnPage)
        );
    }
}
