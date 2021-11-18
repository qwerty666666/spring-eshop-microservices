package com.example.eshop.customer.domain.customer;

import com.example.eshop.customer.domain.customer.Customer.CustomerId;
import com.example.eshop.customer.domain.rbac.Role;
import com.example.eshop.customer.domain.rbac.User;
import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Embedded;
import javax.persistence.AttributeOverride;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Class represents User in App.
 * <p>
 * Each user is unique identified by {@code email}.
 * <p>
 * This class implements {@link User} interface and used
 * to determine user's permissions.
 */
@Table(
        name = "customers",
        uniqueConstraints = @UniqueConstraint(name = "customers_uniq_email", columnNames = "email")
)
@Entity
@Getter
public class Customer extends AggregateRoot<CustomerId> implements User {
    @EmbeddedId
    private CustomerId id;

    @Column(name = "firstname", nullable = false)
    @NotEmpty
    @Size(max = 255)
    private String firstname;

    @Column(name = "lastname", nullable = false)
    @NotEmpty
    @Size(max = 255)
    private String lastname;

    @Embedded
    @AttributeOverride(name = "email", column = @Column(name = "email", nullable = false))
    @Valid
    @NotNull
    private Email email;

    @Embedded
    @AttributeOverride(name = "hashedPassword", column = @Column(name = "password", nullable = false))
    @Valid
    @NotNull
    private HashedPassword password;

    @Column(name = "birthday")
    @Nullable
    private LocalDate birthday;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") },
            indexes = @Index(name = "users_roles_user_id_idx", columnList = "user_id")
    )
    private Set<Role> roles = new HashSet<>();

    protected Customer() {
        this(DomainObjectId.randomId(CustomerId.class));
    }

    private Customer(CustomerId id) {
        this.id = id;
    }

    private Customer(CustomerBuilder builder) {
        this.id = (builder.id == null ? DomainObjectId.randomId(CustomerId.class) : builder.id);
        setFirstname(builder.firstname);
        setLastname(builder.lastname);
        setEmail(builder.email);
        setBirthday(builder.birthday);
        setPassword(builder.password);
    }

    @Override
    public CustomerId getId() {
        return id;
    }

    /**
     * Sets firstname for the Customer
     */
    public void setFirstname(String firstname) {
        Assertions.notEmpty(firstname, "Firstname must be non empty");

        this.firstname = firstname;
    }

    /**
     * Sets lastname for the Customer
     */
    public void setLastname(String lastname) {
        Assertions.notEmpty(lastname, "Lastname must be non empty");

        this.lastname = lastname;
    }

    /**
     * Sets email for the Customer
     */
    public void setEmail(Email email) {
        Assertions.notNull(email, "Email must be not null");

        this.email = email;
    }

    /**
     * Sets password for the Customer.
     */
    public void setPassword(HashedPassword password) {
        Assertions.notNull(password, "Password must be not null");

        this.password = password;
    }

    /**
     * Sets birthday for the Customer
     */
    public void setBirthday(@Nullable LocalDate birthday) {
        this.birthday = birthday;
    }

    /**
     * @return new {@link CustomerBuilder} instance
     */
    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    /**
     * ID object for {@link Customer}
     */
    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CustomerId extends DomainObjectId {
        public CustomerId(String uuid) {
            super(uuid);
        }
    }

    /**
     * Builder for {@link Customer}
     */
    public static class CustomerBuilder {
        private @Nullable CustomerId id;
        private String firstname;
        private String lastname;
        private Email email;
        private HashedPassword password;
        private @Nullable LocalDate birthday;

        CustomerBuilder() {
        }

        public CustomerBuilder id(CustomerId id) {
            this.id = id;
            return this;
        }

        public CustomerBuilder firstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public CustomerBuilder lastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public CustomerBuilder email(Email email) {
            this.email = email;
            return this;
        }

        public CustomerBuilder password(HashedPassword password) {
            this.password = password;
            return this;
        }

        public CustomerBuilder birthday(@Nullable LocalDate birthday) {
            this.birthday = birthday;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
