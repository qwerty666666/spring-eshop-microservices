package com.example.eshop.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.Assert;
import java.util.Collections;

public class WithMockCustomJwtAuthenticationSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomJwtAuthentication> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomJwtAuthentication mockJwtToken) {
        var customerId = mockJwtToken.customerId();
        Assert.notNull(customerId, () -> customerId + " cannot have null customerId property");

        var jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(JwtClaimNames.SUB, customerId)
                .build();

        var authentication = new CustomJwtAuthentication(jwt, Collections.emptyList());

        return createSecurityContext(authentication);
    }

    private SecurityContext createSecurityContext(Authentication authentication) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        return context;
    }
}
