package com.example.eshop.sharedkernel.domain.validation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Errors {
    private Map<String, List<Error>> errors = new LinkedHashMap<>();

    public void addError(String field, String message) {
        List<Error> list = errors.computeIfAbsent(field, s -> new ArrayList<>());

        list.add(new Error(field, message));
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
}
