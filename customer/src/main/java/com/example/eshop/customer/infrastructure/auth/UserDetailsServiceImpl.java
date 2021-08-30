package com.example.eshop.customer.infrastructure.auth;

import com.example.eshop.customer.application.exceptions.CustomerNotFoundException;
import com.example.eshop.customer.application.query.QueryCustomerService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final QueryCustomerService queryCustomerService;

    public UserDetailsServiceImpl(QueryCustomerService queryCustomerService) {
        this.queryCustomerService = queryCustomerService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            var customer = queryCustomerService.getByEmail(email);

            return new UserDetailsImpl(
                    email,
                    customer.getPassword().toString(),
                    customer.getId().toString()
            );
        } catch (CustomerNotFoundException e) {
            throw new UsernameNotFoundException("Customer not found: " + email);
        }
    }
}
