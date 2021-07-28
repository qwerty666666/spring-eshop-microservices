package com.example.eshop.rest.resources;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
    public int status;
    public String detail;

    public ErrorResponse(int status, String detail) {
        this.status = status;
        this.detail = detail;
    }
}
