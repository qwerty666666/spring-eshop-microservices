package com.example.eshop.cart.rest.controllers;

import com.example.eshop.cart.client.model.ValidationErrorDto;
import com.example.eshop.cart.rest.utils.ValidationErrorBuilder;
import com.example.eshop.localizer.Localizer;
import com.example.eshop.sharedkernel.domain.valueobject.InvalidEanFormatException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ControllerAdvice
@RequiredArgsConstructor
public class ErrorControllerAdvice extends ResponseEntityExceptionHandler {
    private final Localizer localizer;

    /**
     * {@link ConstraintViolationException} is thrown by {@link MethodValidationInterceptor}
     * which validates {@link Validated} Controller's method.
     * <p>
     * Idk why Spring distinguish between {@link MethodArgumentNotValidException} and
     * {@link ConstraintViolationException}. See {@link ErrorControllerAdvice#handleMethodArgumentNotValid}
     */
    @ExceptionHandler
    private ResponseEntity<ValidationErrorDto> onConstraintViolationException(ConstraintViolationException e) {
        var errorBuilder = ValidationErrorBuilder.newInstance();

        e.getConstraintViolations().forEach(violation -> {
            var field = getFieldPath(violation);
            var message = violation.getMessage();

            errorBuilder.addError(field, message);
        });

        return ResponseEntity.badRequest().body(errorBuilder.build());
    }

    /**
     * Builds path to field which violates constraint.
     */
    protected String getFieldPath(ConstraintViolation<?> violation) {
        return StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                .map(Node::getName)
                .collect(Collectors.joining("."));
    }

    /**
     * {@link MethodArgumentTypeMismatchException} is thrown when there is an
     * exception during convert Controller's method argument (e.g. converting
     * Ean from String)
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ValidationErrorDto error;

        if (e.getRootCause() instanceof InvalidEanFormatException invalidEanFormatException) {
            error = buildValidationErrorDto(e.getName(), getErrorMessage(invalidEanFormatException));
        } else {
            error = buildValidationErrorDto(e.getName(), getErrorMessage(e));
        }

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Returns default message for {@link MethodArgumentTypeMismatchException}
     */
    protected String getErrorMessage(MethodArgumentTypeMismatchException e) {
        var typeName = Optional.ofNullable(e.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("");

        return "`%s` can't be converted to %s".formatted(e.getValue(), typeName);
    }

    /**
     * {@link MethodArgumentNotValidException} is thrown when {@link ResponseBody}
     * HttpMessageConverter (and some other HttpMessageConverter) fails when
     * validating {@link Validated} Controller's method.
     * <p>
     * Idk why Spring distinguish between {@link MethodArgumentNotValidException} and
     * {@link ConstraintViolationException}. See {@link ErrorControllerAdvice#onConstraintViolationException}
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        var error = ValidationErrorBuilder.newInstance();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            var field = fieldError.getField();
            var message = fieldError.getDefaultMessage();

            error.addError(field, message);
        });

        return handleExceptionInternal(e, error.build(), headers, status, request);
    }

    /**
     * {@link HttpMessageNotReadableException} is thrown when {@link HttpMessageConverter#read}
     * fails (e.g. if jackson deserialization fails)
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        ValidationErrorDto body = null;

        if (e.getCause() instanceof JsonMappingException jsonMappingException) {
            // If there is jackson deserialization error, we try to return
            // response with exact field error information instead of generic
            // jackson error message.
            body = handleJsonMappingException(jsonMappingException);
        }

        if (body != null) {
            return handleExceptionInternal(e, body, headers, status, request);
        }

        return super.handleHttpMessageNotReadable(e, headers, status, request);
    }

    /**
     * Builds response for {@link JsonMappingException}.
     * This exception is thrown when json deserialization is failed for some reason.
     */
    protected ValidationErrorDto handleJsonMappingException(JsonMappingException e) {
        if (e instanceof ValueInstantiationException valueInstantiationException) {
            // This exception is thrown during jackson try to instantiate target object
            // by calling constructor or factory method.
            // For some exceptions we can give more detailed messages in this case,
            // so we handle this exception separately.
            return handleValueInstantiationException(valueInstantiationException);
        }

        return handleJsonMappingExceptionInternal(e);
    }

    /**
     * Builds response for {@link ValueInstantiationException}.
     * This exception is thrown during jackson try to instantiate target object
     * by calling constructor or factory method.
     */
    protected ValidationErrorDto handleValueInstantiationException(ValueInstantiationException e) {
        if (e.getCause() instanceof InvalidEanFormatException invalidEanFormatException) {
            return buildValidationErrorDto(getFieldPath(e), getErrorMessage(invalidEanFormatException));
        }

        return handleJsonMappingExceptionInternal(e);
    }

    /**
     * Builds response for generic {@link JsonMappingException}.
     * This method returns response when we can't provide more detailed info
     * about error. E.g. when user provide String for Integer field.
     */
    protected ValidationErrorDto handleJsonMappingExceptionInternal(JsonMappingException e) {
        return buildValidationErrorDto(getFieldPath(e), e.getOriginalMessage());
    }

    /**
     * Returns error message for {@link InvalidEanFormatException}
     */
    protected String getErrorMessage(InvalidEanFormatException e) {
        return localizer.getMessage("invalidEanFormat", e.getEan());
    }

    /**
     * Builds path to field which cause error while jackson deserialization.
     */
    protected String getFieldPath(JsonMappingException e) {
        return e.getPath().stream()
                .map(Reference::getFieldName)
                .collect(Collectors.joining("."));
    }

    /**
     * Builds {@link ValidationErrorDto} with single field error
     */
    protected ValidationErrorDto buildValidationErrorDto(String field, String message) {
        return ValidationErrorBuilder.newInstance()
                .addError(field, message)
                .build();
    }
}
