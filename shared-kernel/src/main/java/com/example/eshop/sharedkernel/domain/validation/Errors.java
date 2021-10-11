package com.example.eshop.sharedkernel.domain.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Errors implements Iterable<Error> {
    private Map<String, List<Error>> errors = new LinkedHashMap<>();

    public Errors addError(String field, String message) {
        List<Error> list = errors.computeIfAbsent(field, s -> new ArrayList<>());

        list.add(new Error(field, message));

        return this;
    }

    public Errors addErrors(String field, List<Error> errors) {
        List<Error> list = this.errors.computeIfAbsent(field, s -> new ArrayList<>());

        list.addAll(errors);

        return this;
    }

    public Errors addErrors(Errors errors) {
        errors.errors.forEach(this::addErrors);

        return this;
    }

    public boolean hasErrors(String field) {
        return errors.containsKey(field);
    }

    public List<Error> getErrors(String field) {
        return errors.get(field);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    @Override
    public Iterator<Error> iterator() {
        return errors.values().stream()
                .flatMap(Collection::stream)
                .iterator();
    }
}
