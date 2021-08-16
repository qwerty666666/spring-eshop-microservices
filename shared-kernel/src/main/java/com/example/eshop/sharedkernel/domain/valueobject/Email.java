package com.example.eshop.sharedkernel.domain.valueobject;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.ValueObject;
import com.example.eshop.sharedkernel.infrastructure.validation.email.ValidEmail;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email implements ValueObject {
    @Column(name = "email")
    @ValidEmail
    private String email;

    private Email(String email) {
        this.email = email;
    }

    public static Email fromString(String email) {
        Assertions.email(email, "Invalid email");

        return new Email(email);
    }

    @Override
    public String toString() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Email other = (Email) o;

        return email.equals(other.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}
