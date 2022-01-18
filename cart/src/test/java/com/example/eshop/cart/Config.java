package com.example.eshop.cart;

import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.sharedkernel.domain.Localizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@SpringBootApplication
public class Config {
    @Bean
    public ProductCrudService productCrudService() {
        return mock(ProductCrudService.class);
    }

    @Bean
    public Localizer localizer() {
        return mock(Localizer.class);
    }
}
