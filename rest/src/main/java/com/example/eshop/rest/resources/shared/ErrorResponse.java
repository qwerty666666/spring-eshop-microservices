package com.example.eshop.rest.resources.shared;

public class ErrorResponse {
    public int status;
    public String detail;

    public ErrorResponse(int status, String detail) {
        this.status = status;
        this.detail = detail;
    }
}
