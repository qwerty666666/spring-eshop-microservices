package com.example.eshop.sharedkernel.domain.validation;

import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public class Errors implements Iterable<Error>, Serializable {
    private final Map<String, List<Error>> fieldsErrors = new LinkedHashMap<>();

    /**
     * @return new Errors instance with no errors
     */
    public static Errors empty() {
        return new Errors();
    }

    public Errors addError(String field, String message) {
        List<Error> list = fieldsErrors.computeIfAbsent(field, s -> new ArrayList<>());

        list.add(new Error(field, message));

        return this;
    }

    public Errors addErrors(String field, List<Error> errors) {
        List<Error> list = this.fieldsErrors.computeIfAbsent(field, s -> new ArrayList<>());

        list.addAll(errors);

        return this;
    }

    public Errors addErrors(Errors errors) {
        errors.fieldsErrors.forEach(this::addErrors);

        return this;
    }

    public boolean hasErrors(String field) {
        return fieldsErrors.containsKey(field);
    }

    public List<Error> getErrors(String field) {
        return fieldsErrors.get(field);
    }

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
