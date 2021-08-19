package com.example.eshop.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse {
    @JsonProperty
    private List<Error> errors = new ArrayList<>();

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
