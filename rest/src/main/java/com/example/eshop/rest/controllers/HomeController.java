package com.example.eshop.rest.controllers;

import com.example.eshop.rest.utils.UriFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping
    public String home(@Value(UriFactory.SWAGGER_UI_URL_PROPERTY) String swaggerUiUrl) {
        return "forward:" + swaggerUiUrl;
    }
}
