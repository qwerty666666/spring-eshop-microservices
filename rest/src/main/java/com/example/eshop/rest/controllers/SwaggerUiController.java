package com.example.eshop.rest.controllers;

import com.example.eshop.rest.utils.UriFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SwaggerUiController {
    @GetMapping(UriFactory.SWAGGER_UI_URL_PROPERTY)
    public ModelAndView home(@Value("${app.oas.spec-file-url}") String specFileUrl) {
        return new ModelAndView("swagger-ui")
                .addObject("specFileUrl", specFileUrl);
    }
}
