package com.example.eshop.sharedkernel.domain.validation;

import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents validation result.
 * <p>
 * Used as container of field errors {@link Error}.
 */
@EqualsAndHashCode
public class Errors implements Iterable<Error> {
    private final Map<String, List<Error>> fieldsErrors = new LinkedHashMap<>();

    /**
     * @return new Errors instance with no errors
     */
    public static Errors empty() {
        return new Errors();
    }

    /**
     * Adds new {@link Error}
     */
    public Errors addError(String field, String messageCode) {
        return addError(field, messageCode, new Object[0]);
    }

    /**
     * Adds new {@link Error}
     */
    public Errors addError(String field, String messageCode, Object... messageParams) {
        List<Error> list = fieldsErrors.computeIfAbsent(field, s -> new ArrayList<>());

        list.add(new Error(field, messageCode, messageParams));

        return this;
    }

    /**
     * Adds {@link Error}s from the given list
     */
    public Errors addErrors(String field, List<Error> errors) {
        List<Error> list = this.fieldsErrors.computeIfAbsent(field, s -> new ArrayList<>());

        list.addAll(errors);

        return this;
    }

    /**
     * Copy {@link Error}s from given {@code Errors}
     */
    public Errors addErrors(Errors errors) {
        errors.fieldsErrors.forEach(this::addErrors);

        return this;
    }

    /**
     * @return if there are {@link Error}s for given {@code field}
     */
    public boolean hasErrors(String field) {
        return fieldsErrors.containsKey(field);
    }

    /**
     * @return {@link Error}s for given {@code field}
     */
    public List<Error> getErrors(String field) {
        return fieldsErrors.get(field);
    }

    /**
     * @return if there are no {@link Error}s at all
     */
    public boolean isEmpty() {
        return fieldsErrors.isEmpty();
    }

    @Override
    public Iterator<Error> iterator() {
        return fieldsErrors.values().stream()
                .flatMap(Collection::stream)
                .iterator();
    }
}
