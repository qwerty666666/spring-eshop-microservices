package com.example.eshop.testutils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Assertions {
    public static <T1, T2> void assertListEquals(List<T1> list1, List<T2> list2, BiConsumer<T1, T2> itemAssertion) {
        assertThat(list1).hasSize(list2.size());

        for (int i = 0; i < list1.size(); i++) {
            itemAssertion.accept(list1.get(i), list2.get(i));
        }
    }
}
