package com.example.eshop.catalog.rest.controllers;

import com.example.eshop.catalog.client.api.model.ValidationError;
import com.example.eshop.catalog.rest.utils.ValidationErrorBuilder;
import com.example.eshop.localizer.Localizer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;
import java.util.stream.StreamSupport;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final Localizer localizer;

    /**
     * Handle exception if method parameter is invalid
     */
    @ExceptionHandler
    private ResponseEntity<ValidationError> handleInvalidMethodParameterException(InvalidMethodParameterException e) {
        var error = e.getFieldError();

        var validationError = ValidationErrorBuilder.newInstance()
                .addError(error.getField(), localizer.getMessage(error.getMessageCode(), error.getMessageParams()))
                .build();

        return getValidationErrorResponse(validationError);
    }

    /**
     * Handle exception from validating Controller's parameters
     */
    @ExceptionHandler
    private ResponseEntity<ValidationError> onConstraintViolationException(ConstraintViolationException e) {
        var errorBuilder = ValidationErrorBuilder.newInstance();

        e.getConstraintViolations().forEach(violation -> {
            var field = getFieldName(violation);
            var message = violation.getMessage();

            errorBuilder.addError(field, message);
        });

        return getValidationErrorResponse(errorBuilder.build());
    }

    private String getFieldName(ConstraintViolation<?> violation) {
        return StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                // find last Node in Path
                .reduce((prev, next) -> next)
                .map(Node::getName)
                .orElse(null);
    }

    private ResponseEntity<ValidationError> getValidationErrorResponse(ValidationError err) {
        return ResponseEntity.badRequest().body(err);
    }
}
