package com.example.eshop.infrastructure.web.argumentresolvers;

import com.example.eshop.infrastructure.annotations.PageableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Resolver for Pageable with {@link PageableSettings} annotation
 */
@Component
public class PageableWithSettingsArgumentResolver implements HandlerMethodArgumentResolver {
    private final Optional<PageableHandlerMethodArgumentResolverCustomizer> pageableArgumentResolverCustomizer;
    private final Optional<SortHandlerMethodArgumentResolverCustomizer> sortArgumentResolverCustomizer;

    private final Map<PageableSettings, PageableHandlerMethodArgumentResolver> cache = new HashMap<>();

    @Autowired
    public PageableWithSettingsArgumentResolver(
            Optional<PageableHandlerMethodArgumentResolverCustomizer> pageableArgumentResolverCustomizer,
            Optional<SortHandlerMethodArgumentResolverCustomizer> sortArgumentResolverCustomizer) {
        this.pageableArgumentResolverCustomizer = pageableArgumentResolverCustomizer;
        this.sortArgumentResolverCustomizer = sortArgumentResolverCustomizer;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Pageable.class)
                && parameter.getParameterAnnotation(PageableSettings.class) != null;
    }

    @Override
    public Pageable resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        var settings = getPageableSettings(parameter);

        PageableHandlerMethodArgumentResolver resolver = cache.computeIfAbsent(settings, this::createPageableResolver);

        return resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }

    private PageableSettings getPageableSettings(MethodParameter parameter) {
        var settings = parameter.getParameterAnnotation(PageableSettings.class);

        Objects.requireNonNull(settings);

        validatePageableSettings(settings, parameter);

        return settings;
    }

    private void validatePageableSettings(PageableSettings settings, MethodParameter parameter) {
        if (settings.maxPageSize() < 1) {
            throw new IllegalArgumentException(String.format("Invalid max page size for method %s." +
                    "Page size must be positive number", parameter.getMethod()));
        }
    }

    private PageableHandlerMethodArgumentResolver createPageableResolver(PageableSettings settings) {
        // create resolver from global Spring Boot configs

        var sortResolver = new SortHandlerMethodArgumentResolver();
        sortArgumentResolverCustomizer.ifPresent(customizer -> customizer.customize(sortResolver));

        var pageableResolver = new PageableHandlerMethodArgumentResolver(sortResolver);
        pageableArgumentResolverCustomizer.ifPresent(customizer -> customizer.customize(pageableResolver));

        // and rewrite global config with local

        pageableResolver.setMaxPageSize(settings.maxPageSize());
        pageableResolver.setFallbackPageable(PageRequest.of(0, settings.defaultPageSize()));

        return pageableResolver;
    }
}
