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
 * Used as container of field errors {@link FieldError}.
 */
@EqualsAndHashCode
public class Errors implements Iterable<FieldError> {
    private final Map<String, List<FieldError>> fieldsErrors = new LinkedHashMap<>();

    /**
     * @return new Errors instance with no errors
     */
    public static Errors empty() {
        return new Errors();
    }

    /**
     * Adds new {@link FieldError}
     */
    public Errors addError(String field, String messageCode) {
        return addError(field, messageCode, new Object[0]);
    }

    /**
     * Adds new {@link FieldError}
     */
    public Errors addError(String field, String messageCode, Object... messageParams) {
        List<FieldError> list = fieldsErrors.computeIfAbsent(field, s -> new ArrayList<>());

        list.add(new FieldError(field, messageCode, messageParams));

        return this;
    }

    /**
     * Adds {@link FieldError}s from the given list
     */
    public Errors addErrors(String field, List<FieldError> fieldErrors) {
        List<FieldError> list = this.fieldsErrors.computeIfAbsent(field, s -> new ArrayList<>());

        list.addAll(fieldErrors);

        return this;
    }

    /**
     * Copy {@link FieldError}s from given {@code Errors}
     */
    public Errors addErrors(Errors errors) {
        errors.fieldsErrors.forEach(this::addErrors);

        return this;
    }

    /**
     * @return if there are {@link FieldError}s for given {@code field}
     */
    public boolean hasErrors(String field) {
        return fieldsErrors.containsKey(field);
    }

    /**
     * @return {@link FieldError}s for given {@code field}
     */
    public List<FieldError> getErrors(String field) {
        return fieldsErrors.get(field);
    }

    /**
     * @return if there are no {@link FieldError}s at all
     */
    public boolean isEmpty() {
        return fieldsErrors.isEmpty();
    }

    @Override
    public Iterator<FieldError> iterator() {
        return fieldsErrors.values().stream()
                .flatMap(Collection::stream)
                .iterator();
    }
}
