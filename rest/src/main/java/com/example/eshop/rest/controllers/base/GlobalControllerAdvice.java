package com.example.eshop.rest.controllers.base;

import com.example.eshop.rest.resources.ValidationErrorResponse;
import com.example.eshop.rest.resources.ValidationErrorResponse.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var error = new ValidationErrorResponse();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            error.addError(new Error(fieldError.getField(), fieldError.getDefaultMessage()));
        });

        return error;
    }
}
