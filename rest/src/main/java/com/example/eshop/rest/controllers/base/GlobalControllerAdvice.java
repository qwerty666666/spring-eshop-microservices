package com.example.eshop.rest.controllers.base;

import com.example.eshop.rest.dto.ValidationErrorDto;
import com.example.eshop.rest.infrastructure.converters.EanParameterFormatter;
import com.example.eshop.rest.infrastructure.converters.EanParameterInvalidFormatException;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;
import java.util.Locale;
import java.util.stream.StreamSupport;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private MessageSource messageSource;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addCustomFormatter(new EanParameterFormatter());
    }

    /**
     * Handle exceptions from validating Controller method's arguments
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ValidationErrorDto onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var error = ValidationErrorBuilder.newInstance();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            var field = fieldError.getField();
            var message = fieldError.getDefaultMessage();

            error.addError(field, message);
        });

        return error.build();
    }

    /**
     * Handle exception from binding {@link Ean} parameter to Controller methods
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ValidationErrorDto onMethodArgumentNotValidException(EanParameterInvalidFormatException e, Locale locale) {
        return ValidationErrorBuilder.newInstance()
                .addError("ean", messageSource.getMessage("invalidEanFormat", new Object[]{ e.getEan() }, locale))
                .build();
    }

    /**
     * Handle exception from validating Controller's parameters
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ValidationErrorDto onConstraintViolationException(ConstraintViolationException e) {
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

    /**
     * Handle exception from Domain Validation
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ValidationErrorDto onValidationException(ValidationException e) {
        var error = ValidationErrorBuilder.newInstance();

        e.getErrors().forEach(err -> error.addError(err.getField(), err.getMessage()));

        return error.build();
    }
}
