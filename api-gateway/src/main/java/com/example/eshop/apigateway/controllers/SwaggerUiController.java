package com.example.eshop.apigateway.controllers;

import com.example.eshop.apigateway.config.AppProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerUiController {
    /**
     * Swagger UI page
     */
    @GetMapping(AppProperties.SWAGGER_UI_URL_PROPERTY)
    public String swaggerUi(Model model, @Value(AppProperties.SWAGGER_SPEC_FILE_LOCATION) String specFileUrl) {
        model.addAttribute("specFileUrl", specFileUrl);

        return "swagger-ui";
    }
}
