package com.example.eshop.customer.domain.rbac;

import java.util.Collection;

/**
 * Represents a User in App. Each User have set of {@link Role}.
 * <p>
 * User is allowed to make some operation only if he contains {@link Role}
 * which has required {@link Permission} for this operation.
 */
public interface User {
    /**
     * @return set of user roles.
     */
    Collection<Role> getRoles();
}
