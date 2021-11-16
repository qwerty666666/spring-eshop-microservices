package com.example.eshop.rest.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * ResourceTransformer that replaces url to specification file
 * in Swagger UI webjar resource.
 */
@RequiredArgsConstructor
public class SwaggerUiResourceTransformer implements ResourceTransformer {
    private final String specUrl;

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {
        var html = new String(resource.getInputStream().readAllBytes());

        html = replaceDefaultSpecificationUrl(html);

        return new TransformedResource(resource, html.getBytes());
    }

    private String replaceDefaultSpecificationUrl(String html) {
        return html.replace("https://petstore.swagger.io/v2/swagger.json", specUrl);
    }
}
