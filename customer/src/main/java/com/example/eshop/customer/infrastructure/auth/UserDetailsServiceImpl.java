package com.example.eshop.customer.infrastructure.auth;

import com.example.eshop.customer.application.exceptions.CustomerNotFoundException;
import com.example.eshop.customer.application.query.QueryCustomerService;
import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.rbac.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final QueryCustomerService queryCustomerService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer;

        try {
            customer = queryCustomerService.getByEmail(email);
        } catch (CustomerNotFoundException e) {
            throw new UsernameNotFoundException("Customer not found: " + email);
        }

        return new UserDetailsImpl(
                email,
                customer.getPassword().toString(),
                customer.getId().toString(),
                buildGrantedAuthorities(customer)
        );
    }

    private List<? extends GrantedAuthority> buildGrantedAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // add permissions
        authorities.addAll(
                user.getRoles().stream()
                        .flatMap(role -> role.getPermissions().stream())
                        .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                        .toList()
        );

        // add roles (ROLE_*)
        authorities.addAll(
                user.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName())
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );

        return authorities;
    }
}
