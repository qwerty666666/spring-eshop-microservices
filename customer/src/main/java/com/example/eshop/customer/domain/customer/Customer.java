package com.example.eshop.customer.domain.customer;

import com.example.eshop.customer.domain.customer.Customer.CustomerId;
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
import javax.persistence.Table;
import javax.persistence.Embedded;
import javax.persistence.AttributeOverride;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Table(name = "customers")
@Entity
@Getter
public class Customer extends AggregateRoot<CustomerId> {
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
    private Email email;

    @Embedded
    @AttributeOverride(name = "hashedPassword", column = @Column(name = "password", nullable = false))
    @Valid
    private HashedPassword password;

    @Column(name = "birthday")
    @Nullable
    private LocalDate birthday;

    protected Customer() {
        this(DomainObjectId.randomId(CustomerId.class));
    }

    private Customer(CustomerId id) {
        this.id = id;
    }

    @Override
    public CustomerId getId() {
        return id;
    }

    public void setFirstname(String firstname) {
        Assertions.notEmpty(firstname, "Firstname must be non empty");
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        Assertions.notEmpty(lastname, "Lastname must be non empty");
        this.lastname = lastname;
    }

    public void setEmail(Email email) {
        Assertions.notNull(email, "Email must be not null");
        this.email = email;
    }

    public void setPassword(HashedPassword password) {
        Assertions.notNull(password, "Password must be not null");
        this.password = password;
    }

    public void setBirthday(@Nullable LocalDate birthday) {
        this.birthday = birthday;
    }

    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CustomerId extends DomainObjectId {
        public CustomerId(String uuid) {
            super(uuid);
        }
    }

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
            var customer = (id == null ? new Customer() : new Customer(id));
            customer.setFirstname(firstname);
            customer.setLastname(lastname);
            customer.setEmail(email);
            customer.setBirthday(birthday);
            customer.setPassword(password);

            return customer;
        }
    }
}
