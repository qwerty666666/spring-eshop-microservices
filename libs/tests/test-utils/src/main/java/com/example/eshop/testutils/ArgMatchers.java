package com.example.eshop.testutils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mockito.ArgumentMatcher;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArgMatchers {
    /**
     * Verifies that the actual collection contains only values
     * from the given {@code expected} collection and nothing else.
     */
    public static <T> ArgumentMatcher<List<T>> listContainsExactlyInAnyOrder(T... expected) {
        return actual -> actual.size() == expected.length && actual.containsAll(Arrays.asList(expected));
    }
}
