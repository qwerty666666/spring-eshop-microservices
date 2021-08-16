package com.example.eshop.sharedkernel.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class Assertions {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    /**
     * Given String is not null and well-formed email
     * @throws IllegalArgumentException
     */
    public static void email(String email, String message) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Given string is not null or empty
     * @throws IllegalArgumentException
     */
    public static void notEmpty(String s, String message) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Given object is not null
     * @throws IllegalArgumentException
     */
    public static void notNull(Object o, String message) {
        if (Objects.isNull(o)) {
            throw new IllegalArgumentException(message);
        }
    }
}
