package com.example.eshop.customer.domain.rbac;

import lombok.Getter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents set of {@link Permission}
 * grouped together.
 * <p>
 * Roles are assigned to {@link User}.
 */
@Entity
@Table(name = "roles")
@Getter
public class Role  {
    /**
     * Administrator Role name
     */
    public static final String ADMIN = "ADMIN";

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ROLES_PERMISSIONS",
            joinColumns = {@JoinColumn(name = "ROLE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "PERMISSION_ID")}
    )
    private Set<Permission> permissions = new HashSet<>();

    /**
     * @return set of {@link Permission} for this role
     */
    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }
}

