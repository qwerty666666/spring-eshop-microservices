package com.example.eshop.cart.rest.controllers;

import com.example.eshop.cart.client.api.model.ValidationErrorDto;
import com.example.eshop.cart.rest.utils.ValidationErrorBuilder;
import com.example.eshop.localizer.Localizer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;
import java.util.stream.StreamSupport;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final Localizer localizer;

    /**
     * Handle exception if current user is unauthorized
     */
    @ExceptionHandler(NotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private void handleNotAuthenticatedException() {
        // Unauthorized response should be returned by SecurityConfig.
        // This method is for safety purpose only to return 401 instead of 500
        // in the case of misconfiguration
    }

    /**
     * Handle exception if method parameter is invalid
     */
    @ExceptionHandler
    private ResponseEntity<ValidationErrorDto> handleInvalidMethodParameterException(InvalidMethodParameterException e) {
        var errorBuilder = ValidationErrorBuilder.newInstance();

        e.getFieldErrors().forEach(fieldError -> {
            var errorMessage = localizer.getMessage(fieldError.getMessageCode(), fieldError.getMessageParams());
            errorBuilder.addError(fieldError.getField(), errorMessage);
        });

        return getValidationErrorResponse(errorBuilder.build());
    }

    /**
     * Handle exception from validating Controller's parameters
     */
    @ExceptionHandler
    private ResponseEntity<ValidationErrorDto> onConstraintViolationException(ConstraintViolationException e) {
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

    private ResponseEntity<ValidationErrorDto> getValidationErrorResponse(ValidationErrorDto err) {
        return ResponseEntity.badRequest().body(err);
    }
}
