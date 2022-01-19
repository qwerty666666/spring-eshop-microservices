package com.example.eshop.catalog.rest.controllers;

import com.example.eshop.catalog.client.api.model.ValidationError;
import com.example.eshop.catalog.rest.utils.ValidationErrorBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;
import java.util.stream.StreamSupport;

@ControllerAdvice
public class GlobalControllerAdvice {
    /**
     * Handle exception from validating Controller's parameters
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ValidationError onConstraintViolationException(ConstraintViolationException e) {
        var error = ValidationErrorBuilder.newInstance();

        e.getConstraintViolations().forEach(violation -> {
            var field = getFieldName(violation);
            var message = violation.getMessage();

            error.addError(field, message);
        });

        return error.build();
    }

    private String getFieldName(ConstraintViolation<?> violation) {
        return StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                // find last Node in Path
                .reduce((prev, next) -> next)
                .map(Node::getName)
                .orElse(null);
    }
}
