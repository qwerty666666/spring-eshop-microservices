package com.example.eshop.apigateway.controllers;

public record BasicErrorResponse(
    int status,
    String details
) {
}
