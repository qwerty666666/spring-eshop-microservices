package com.example.eshop.catalog.rest.mappers;

import com.example.eshop.catalog.client.api.model.PageableDto;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {
    public static <T1, T2> void assertListEquals(List<T1> list1, List<T2> list2, BiConsumer<T1, T2> itemAssertion) {
        assertThat(list1).as("list size").hasSize(list2.size());

        for (int i = 0; i < list1.size(); i++) {
            itemAssertion.accept(list1.get(i), list2.get(i));
        }
    }

    public static void assertPageableEquals(Page<?> page, PageableDto pageableDto) {
        assertThat(pageableDto.getPage()).as("page number").isEqualTo(page.getNumber() + 1);
        assertThat(pageableDto.getPerPage()).as("page size").isEqualTo(page.getSize());
        assertThat(pageableDto.getTotalPages()).as("total pages").isEqualTo(page.getTotalPages());
        assertThat(pageableDto.getTotalItems()).as("total items").isEqualTo((int)page.getTotalElements());
    }
}
