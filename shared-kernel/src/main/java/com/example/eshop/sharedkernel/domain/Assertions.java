package com.example.eshop.sharedkernel.domain;

import java.util.regex.Pattern;

public class Assertions {
    // TODO replace with RFC complaint, or with external lib
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    /**
     * Given String is not null and well-formed email
     *
     * @throws IllegalArgumentException if {@code email} is null or bad formed
     */
    public static void email(String email, String message) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Given string is not null or empty
     *
     * @throws IllegalArgumentException if {@code s} is null or empty
     */
    public static void notEmpty(String s, String message) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Given object is not null
     *
     * @throws IllegalArgumentException if {@code o} is null
     */
    public static void notNull(Object o, String message) {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Given {@code num} must be greater or equal to zero
     *
     * @throws IllegalArgumentException if {@code num} is negative
     */
    public static void nonNegative(int num, String message) {
        if (num < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Given {@code num} must be positive
     *
     * @throws IllegalArgumentException if {@code num} is negative or zero
     */
    public static void positive(int num, String message) {
        if (num <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
