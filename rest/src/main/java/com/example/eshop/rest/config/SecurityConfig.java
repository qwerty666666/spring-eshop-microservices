package com.example.eshop.rest.config;

import com.example.eshop.customer.domain.rbac.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration("rest-securityConfig")
@Import(com.example.eshop.customer.config.SecurityConfig.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        // require for h2 console
                        .frameOptions().disable()
                )
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .httpBasic().and()
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeRequests(requests -> requests
                        // /api Endpoints
                        .antMatchers("/api/categories/**").permitAll()
                        .antMatchers("/api/products/**").permitAll()
                        .antMatchers(HttpMethod.POST, "/api/customers").permitAll()
                        .antMatchers("/api/**").authenticated()
                        // Actuator Endpoints
                        .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ADMIN)
                        // Others
                        .anyRequest().permitAll()
                );
    }
}
