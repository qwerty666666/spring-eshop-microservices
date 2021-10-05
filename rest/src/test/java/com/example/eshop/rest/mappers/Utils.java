package com.example.eshop.rest.mappers;

import com.example.eshop.rest.dto.MoneyDto;
import com.example.eshop.rest.dto.PageableDto;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class Utils {
    public static void assertPageableEquals(Page<?> page, PageableDto pageableDto) {
        assertThat(pageableDto.getPage()).as("page number").isEqualTo(page.getNumber() + 1);
        assertThat(pageableDto.getPerPage()).as("page size").isEqualTo(page.getSize());
        assertThat(pageableDto.getTotalPages()).as("total pages").isEqualTo(page.getTotalPages());
        assertThat(pageableDto.getTotalItems()).as("total items").isEqualTo(page.getTotalElements());
    }

    public static <T1, T2> void assertListEquals(List<T1> list1, List<T2> list2, BiConsumer<T1, T2> itemAssertion) {
        assertThat(list1).as("list size").hasSize(list2.size());

        for (int i = 0; i < list1.size(); i++) {
            itemAssertion.accept(list1.get(i), list2.get(i));
        }
    }

    public static void assertPriceEquals(Money money, MoneyDto moneyDto) {
        assertThat(moneyDto.getAmount()).as("price amount").isEqualTo(money.getAmount());
        assertThat(moneyDto.getCurrency()).as("price currency").isEqualTo(money.getCurrency().getCurrencyCode());
    }
}
