package com.example.eshop.sharedtest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mockito.ArgumentMatcher;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArgMatchers {
    /**
     * Verifies that the actual collection contains exactly the values
     * of the given {@code expected} collection and nothing else.
     */
    public static <T> ArgumentMatcher<Collection<T>> containsExactlyInAnyOrder(Collection<T> expected) {
        return (Collection<T> actual) -> actual.size() == expected.size() &&
                expected.containsAll(actual);
    }
}
