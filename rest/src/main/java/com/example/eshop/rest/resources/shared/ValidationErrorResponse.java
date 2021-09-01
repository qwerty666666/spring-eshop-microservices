package com.example.eshop.rest.resources.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse {
    @JsonProperty
    private List<Error> errors = new ArrayList<>();

    public ValidationErrorResponse addError(String field, String message) {
        return addError(new Error(field, message));
    }

    public ValidationErrorResponse addError(Error error) {
        errors.add(error);
        return this;
    }

    @AllArgsConstructor
    public static class Error {
        public String field;
        public String message;
    }
}
