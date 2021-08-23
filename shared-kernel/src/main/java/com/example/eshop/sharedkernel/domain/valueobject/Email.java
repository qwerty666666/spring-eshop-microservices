package com.example.eshop.sharedkernel.domain.valueobject;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.ValueObject;
import com.example.eshop.sharedkernel.infrastructure.validation.email.ValidEmail;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email implements ValueObject {
    @Column(name = "email")
    @ValidEmail
    private String email;

    private Email(String email) {
        this.email = email;
    }

    /**
     * Creates email from given String
     *
     * @throws IllegalArgumentException if email is not well-formed email String
     */
    public static Email fromString(String email) {
        Assertions.email(email, "Invalid email");

        return new Email(email);
    }

    /**
     * @return email string
     */
    @Override
    public String toString() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Email email1 = (Email) o;

        return Objects.equals(email, email1.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}
