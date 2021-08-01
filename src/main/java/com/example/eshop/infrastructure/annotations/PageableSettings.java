package com.example.eshop.infrastructure.annotations;

import java.lang.annotation.*;

/**
 * Annotation to set config for single {@link org.springframework.web.bind.annotation.RequestMapping}
 * when injecting {@link org.springframework.data.domain.Pageable} into a controller method.
 *
 * It is helpful as Spring Boot {@link org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties}
 * only allows to set settings to whole application at once.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PageableSettings {
    /**
     * Max allowed page size
     */
    int maxPageSize() default 2000;
}
