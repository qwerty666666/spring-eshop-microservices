package com.example.eshop.rest.controllers.base;

import com.example.eshop.rest.infrastructure.converters.EanParameterFormatter;
import com.example.eshop.rest.infrastructure.converters.EanParameterInvalidFormatException;
import com.example.eshop.rest.resources.shared.ValidationErrorResponse;
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
import java.util.Locale;

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
    ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var error = new ValidationErrorResponse();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            error.addError(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return error;
    }

    /**
     * Handle exception from binding {@link Ean} parameter to Controller methods
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(EanParameterInvalidFormatException e, Locale locale) {
        var message = messageSource.getMessage("invalidEanFormat", new Object[]{ e.getEan() }, locale);

        return new ValidationErrorResponse().addError("ean", message);
    }
}
