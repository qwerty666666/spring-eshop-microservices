package com.example.eshop.apigateway.controllers;

import com.example.eshop.apigateway.config.AppProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    /**
     * Home page (redirect to Swagger UI)
     */
    @GetMapping
    public String home(@Value(AppProperties.SWAGGER_UI_URL_PROPERTY) String swaggerUiUrl) {
        return "redirect:" + swaggerUiUrl;
    }
}
