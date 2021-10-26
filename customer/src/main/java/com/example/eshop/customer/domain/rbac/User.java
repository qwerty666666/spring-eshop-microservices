package com.example.eshop.customer.domain.rbac;

import java.util.Collection;

public interface User {
    Collection<Role> getRoles();
}
