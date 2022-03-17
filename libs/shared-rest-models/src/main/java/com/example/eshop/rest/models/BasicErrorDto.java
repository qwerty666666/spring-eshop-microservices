package com.example.eshop.rest.models;

public record BasicErrorDto(
        int status,
        String detail
) {
    public BasicErrorDto(int status, String detail) {
        if (status < 100 || status >= 600) {
            throw new IllegalArgumentException("Invalid status code " + status);
        }

        this.status = status;
        this.detail = detail == null ? "" : detail;
    }
}
